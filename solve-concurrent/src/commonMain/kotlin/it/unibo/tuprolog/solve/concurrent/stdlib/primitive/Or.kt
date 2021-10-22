package it.unibo.tuprolog.solve.concurrent.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.concurrent.ConcurrentSolver
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import kotlinx.coroutines.channels.Channel as KtChannel

object Or : BinaryRelation.WithoutSideEffects<ConcurrentExecutionContext>(";") {
    override fun Solve.Request<ConcurrentExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term
    ): Sequence<Substitution> {
        ensuringArgumentIsCallable(0)
        ensuringArgumentIsCallable(1)
        val solver1: ConcurrentSolver = context.createSolver()
        val solver2: ConcurrentSolver = context.createSolver()
        val channel = KtChannel<Solution>(KtChannel.UNLIMITED)
        val options = SolveOptions.DEFAULT.setOption("keepChannelOpen", true)
        solver1.solveConcurrently(first.castToStruct(), SolveOptions.DEFAULT, channel)
        solver2.solveConcurrently(second.castToStruct(), SolveOptions.DEFAULT, channel)
        return solver1.solve(first.castToStruct()).map { it.substitution } +
            solver2.solve(second.castToStruct()).map { it.substitution }
    }
}