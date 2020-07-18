package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.stdlib.DefaultBuiltins
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestMutableSolver : SolverFactory {
    @Test
    fun retractAllAcceptingNonGroundClausesRemovesAllMatchingClausesFromMutableSolver() {
        val solver = mutableSolverWithDefaultBuiltins()

        val ma = Struct.of("m", Atom.of("a"))

        solver.assertZ(ma)

        var solutions = solver.solve(ma).toList()
        assertTrue { solutions.size == 1 && solutions[0] is Solution.Yes }

        val result = solver.retractAll(Struct.of("m", Var.anonymous()))
        assertEquals(
            RetractResult.Success(Theory.empty(), listOf(Fact.of(ma))),
            result
        )

        solutions = solver.solve(ma).toList()
        assertTrue { solutions.size == 1 && solutions[0] is Solution.No }
    }

    override val defaultBuiltins: AliasedLibrary
        get() = DefaultBuiltins

    override fun solverOf(
        libraries: Libraries,
        flags: PrologFlags,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ) = ClassicSolverFactory.solverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

    override fun mutableSolverOf(
        libraries: Libraries,
        flags: PrologFlags,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ) =  ClassicSolverFactory.mutableSolverOf(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)
}