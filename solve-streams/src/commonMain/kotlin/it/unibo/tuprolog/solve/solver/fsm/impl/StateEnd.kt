package it.unibo.tuprolog.solve.solver.fsm.impl

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.SideEffect
import it.unibo.tuprolog.solve.SideEffectManager
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.solver.fsm.AbstractState
import it.unibo.tuprolog.solve.solver.fsm.FinalState
import it.unibo.tuprolog.solve.solver.fsm.IntermediateState
import it.unibo.tuprolog.solve.solver.fsm.State
import it.unibo.tuprolog.solve.solver.getSideEffectManager

/**
 * Base class of states representing the computation end
 *
 * @param solve The [Solve.Response] characterizing this Final State
 *
 * @author Enrico
 */
internal sealed class StateEnd(
    protected open val sourceContext: StreamsExecutionContext,
    override val solve: Solve.Response
) : AbstractState(solve), FinalState {

    override fun behave(): Sequence<State> = emptySequence()

    override val context: StreamsExecutionContext by lazy {
        sourceContext.apply(solve.sideEffects).copy(
            substitution = solve.solution.substitution as? Substitution.Unifier ?: Substitution.empty()
        )
    }

    /** The *True* state is reached when a successful computational path has ended */
    internal data class True(
        override val sourceContext: StreamsExecutionContext,
        override val solve: Solve.Response
    ) : StateEnd(sourceContext, solve), FinalState {
        init {
            require(solve.solution is Solution.Yes) { "True end state can be created only with Solution.Yes. Current: `${solve.solution}`" }
        }
    }

    /** The *False* state is reached when a failed computational path has ended */
    internal data class False(
        override val sourceContext: StreamsExecutionContext,
        override val solve: Solve.Response
    ) : StateEnd(sourceContext, solve), FinalState {
        init {
            require(solve.solution is Solution.No) { "False end state can be created only with Solution.No. Current: `${solve.solution}`" }
        }
    }

    /** The *Halt* state is reached when an [HaltException] is caught, terminating the computation */
    internal data class Halt(
        override val sourceContext: StreamsExecutionContext,
        override val solve: Solve.Response
    ) : StateEnd(sourceContext, solve), FinalState {
        init {
            require(solve.solution is Solution.Halt) { "Halt end state can be created only with Solution.Halt. Current: `${solve.solution}`" }
        }

        /** Shorthand property to access `solve.solution.exception` */
        val exception: TuPrologRuntimeException by lazy { (solve.solution as Solution.Halt).exception }
    }
}

/** Transition from this intermediate state to [StateEnd.True], creating a [Solve.Response] with given data */
internal fun IntermediateState.stateEndTrue(
    substitution: Substitution.Unifier = Substitution.empty(),
    otherContext: StreamsExecutionContext? = null,
    sideEffectManager: SideEffectManager? = null,
    vararg sideEffects: SideEffect
) = StateEnd.True(
    otherContext ?: context,
    solve.replySuccess(
        substitution,
        sideEffectManager ?: solve.context.getSideEffectManager(),
        *sideEffects
    )
)

/** Transition from this intermediate state to [StateEnd.False], creating a [Solve.Response] with given data */
internal fun IntermediateState.stateEndFalse(
    otherContext: StreamsExecutionContext? = null,
    sideEffectManager: SideEffectManager? = null,
    vararg sideEffects: SideEffect
) =
    StateEnd.False(
        otherContext ?: context,
        solve.replyFail(
            sideEffectManager ?: solve.context.getSideEffectManager(),
            *sideEffects
        )
    )

/** Transition from this intermediate state to [StateEnd.Halt], creating a [Solve.Response] with given data */
internal fun IntermediateState.stateEndHalt(
    exception: TuPrologRuntimeException,
    otherContext: StreamsExecutionContext? = null,
    sideEffectManager: SideEffectManager? = null,
    vararg sideEffects: SideEffect
) = StateEnd.Halt(
    otherContext ?: context,
    solve.replyException(
        exception,
        sideEffectManager
            ?: exception.context.getSideEffectManager()
            ?: solve.context.getSideEffectManager(),
        *sideEffects
    )
)

/** Transition from this intermediate state to the correct [StateEnd] depending on provided [solution] */
internal fun IntermediateState.stateEnd(
    solution: Solution,
    otherContext: StreamsExecutionContext? = null,
    sideEffectManager: SideEffectManager? = null,
    vararg sideEffects: SideEffect
): StateEnd = when (solution) {
    is Solution.Yes ->
        stateEndTrue(
            solution.substitution.takeUnless { it.isEmpty() } ?: solve.context.substitution,
            otherContext,
            sideEffectManager,
            *sideEffects
        )
    is Solution.No -> stateEndFalse(otherContext, sideEffectManager, *sideEffects)
    is Solution.Halt -> stateEndHalt(solution.exception, otherContext, sideEffectManager, *sideEffects)
}

/** Transition from this intermediate state to a [StateEnd] containing provided [response] data */
internal fun IntermediateState.stateEnd(
    response: Solve.Response,
    otherContext: StreamsExecutionContext? = null
) = with(response) {
    stateEnd(solution, otherContext, response.sideEffectManager, *response.sideEffects.toTypedArray())
}
