package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestMutableSolver
import kotlin.test.Test

class TestClassicMutableSolver : TestMutableSolver, SolverFactory by ClassicSolverFactory {

    private val prototype = TestMutableSolver.prototype(this)

    @Test
    override fun testAssertA() {
        prototype.testAssertA()
    }

    @Test
    override fun testAssertZ() {
        prototype.testAssertZ()
    }

    @Test
    override fun testLoadLibrary() {
        prototype.testLoadLibrary()
    }

    @Test
    override fun testSetFlag() {
        prototype.testSetFlag()
    }
}
