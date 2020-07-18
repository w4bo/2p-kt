package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.testutils.ClauseAssertionUtils
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertClausesHaveSameLengthAndContent
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [RetractResult] and subclasses
 *
 * @author Enrico
 */
internal class PrototypeRetractResultTest(
    private val emptyTheoryGenerator: () -> Theory,
    private val theoryGenerator: (Iterable<Clause>) -> Theory
) {

    private val clause1 = Clause.of(Struct.of("a", Var.anonymous()), Struct.of("b", Var.anonymous()))
    private val clause2 = Clause.of(Struct.of("p", Atom.of("john")))

    private val clauses = listOf(clause1, clause2)

    private val theory: Theory = theoryGenerator(clauses)

    private val toTestSuccess = RetractResult.Success(theory, clauses)
    private val toTestFailure = RetractResult.Failure(theory)

    fun successTheoryCorrect() {
        assertEquals(theory, toTestSuccess.theory)
    }

    fun successClausesListCorrect() {
        assertEquals(clauses, toTestSuccess.clauses)
    }

    fun successFirstClauseCorrect() {
        assertEquals(clauses.first(), toTestSuccess.firstClause)
    }

    fun successFirstClauseWithEmptyClauseListThrowsException() {
        assertFailsWith<NoSuchElementException> {
            RetractResult.Success(theory, emptyList()).firstClause
        }
    }

    fun failTheoryCorrect() {
        assertEquals(theory, toTestFailure.theory)
    }

    fun retractAllSupportsPatterns() {
        val ma = Struct.of("m", Atom.of("a"))
        var theory = emptyTheoryGenerator()
        assertClausesHaveSameLengthAndContent(
            emptySequence(),
            theory.asSequence()
        )
        theory = theory.assertZ(ma)
        assertClausesHaveSameLengthAndContent(
            sequenceOf(Fact.of(ma)),
            theory.asSequence()
        )
        val result = theory.retractAll(Struct.of("m", Var.anonymous()))
        assertEquals(
            RetractResult.Success(emptyTheoryGenerator(), listOf(Fact.of(ma))),
            result
        )
        theory = result.theory
        assertEquals(
            RetractResult.Failure(theory),
            theory.retractAll(ma)
        )
    }
}
