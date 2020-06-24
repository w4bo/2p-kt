package it.unibo.tuprolog.solve.solver.fsm

import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.Solve.Response
import it.unibo.tuprolog.solve.solver.StreamsExecutionContext

/**
 * Represents an Intermediate [State] of the Prolog solver state-machine
 *
 * @author Enrico
 */
internal interface IntermediateState : State {

    /** The [Solve.Request] that drives the State behaviour towards [Response]s */
    override val solve: Solve.Request<StreamsExecutionContext>

    override val context: StreamsExecutionContext
        get() = solve.context

}
