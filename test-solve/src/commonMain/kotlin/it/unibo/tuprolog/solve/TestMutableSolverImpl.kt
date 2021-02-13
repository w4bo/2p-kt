package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.dsl.prolog
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestMutableSolverImpl(private val solverFactory: SolverFactory) : TestMutableSolver {

    private fun testAssert(reverse: Boolean, assert: MutableSolver.(Clause) -> Unit) {
        prolog {
            val solver = solverFactory.mutableSolverOf()
            assertEquals(ktEmptyList(), solver.dynamicKb["canary"(X)].toList())
            solver.assert(fact { "canary"(1) })
            assertEquals(1, solver.dynamicKb["canary"(X)].count())
            solver.solveOnce("fail"("fast"))
            solver.assert(fact { "canary"(2) })
            assertEquals(
                ktListOf(fact { "canary"(1) }, fact { "canary"(2) }).let {
                    if (reverse) it.asReversed() else it
                },
                solver.dynamicKb["canary"(X)].toList()
            )
        }
    }

    override fun testAssertA() {
        testAssert(true) {
            assertA(it)
        }
    }

    override fun testAssertZ() {
        testAssert(false) {
            assertZ(it)
        }
    }

    override fun testLoadLibrary() {
        prolog {
            val solver = solverFactory.mutableSolverOf()
            assertTrue { solver.libraries.isEmpty() }
            solver.loadLibrary(solverFactory.defaultBuiltins)
            assertTrue { solverFactory.defaultBuiltins.alias in solver.libraries }
            solver.solveOnce("fail"("fast"))
            assertTrue { solverFactory.defaultBuiltins.alias in solver.libraries }
        }
    }

    override fun testSetFlag() {
        prolog {
            val solver = solverFactory.mutableSolverOf()
            assertEquals(solverFactory.defaultFlags, solver.flags)
            assertTrue { "someFlag" !in solver.flags }
            solver.setFlag("someFlag", Integer.ZERO)
            assertTrue { solver.flags.entries.any { (k, v) -> k == "someFlag" && v == Integer.ZERO } }
            solver.solveOnce("fail"("fast"))
            assertTrue { solver.flags.entries.any { (k, v) -> k == "someFlag" && v == Integer.ZERO } }
        }
    }
}
