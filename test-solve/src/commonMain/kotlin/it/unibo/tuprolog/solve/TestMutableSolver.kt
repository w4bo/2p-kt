package it.unibo.tuprolog.solve

interface TestMutableSolver : SolverTest {

    companion object {
        fun prototype(solverFactory: SolverFactory): TestMutableSolver =
            TestMutableSolverImpl(solverFactory)
    }

    fun testAssertA()

    fun testAssertZ()

    fun testLoadLibrary()

    fun testSetFlag()
}
