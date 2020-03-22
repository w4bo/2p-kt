package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Set

internal abstract class AbstractTermFormatter : TermFormatter {

    override fun defaultValue(term: Term): String {
        return term.toString()
    }

    override fun visitStruct(term: Struct): String =
        (if (term.isFunctorWellFormed) term.functor else Struct.escapeFunctor(term.functor)) +
                term.argsSequence.map { it.accept(childFormatter()) }.joinToString(", ", "(", ")")

    override fun visitSet(term: Set): String =
        term.unfoldedList.joinToString(", ", "{", "}") {
            it.accept(itemFormatter())
        }

    protected open fun itemFormatter(): TermFormatter = this

    protected open fun childFormatter(): TermFormatter = this

    override fun visitCons(term: Cons): String =
        with(term.unfoldedList) {
            val last = last()
            val base = subList(0, lastIndex).joinToString(", ", "[", "") {
                it.accept(itemFormatter())
            }
            val lastString = if (last is EmptyList) {
                "]"
            } else {
                " | ${last.accept(itemFormatter())}]"
            }
            return base + lastString
        }

    override fun visitTuple(term: Tuple): String =
        visitStruct(term)

    override fun visitRule(term: Rule): String =
        visitStruct(term)

    override fun visitFact(term: Fact): String =
        visitStruct(term)

    override fun visitDirective(term: Directive): String =
        visitStruct(term)

    override fun visitIndicator(term: Indicator): String =
        visitStruct(term)
}