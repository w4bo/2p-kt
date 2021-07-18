package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.setTags

internal class PatternImpl(
    override val functor: Var,
    override val args: List<Term>,
    tags: Map<String, Any> = emptyMap()
) : AbstractComposed(functor, args, tags), Pattern {

    override fun freshCopy(): Pattern = super.freshCopy().castToPattern()

    override fun freshCopy(scope: Scope): Pattern = when {
        isGround -> this
        else -> scope.patternOf(functor, argsSequence.map { it.freshCopy(scope) }).setTags(tags)
    }

    override fun structurallyEquals(other: Term): Boolean =
        other.isPattern && other.castToPattern().let {
            arity == it.arity && itemsAreStructurallyEqual(it)
        }

    override val variables: Sequence<Var>
        get() = sequenceOf(functor) + super.variables

    override val functorToString: String
        get() = functor.toString()

    override fun addLast(argument: Term): Pattern = super.addLast(argument).castToPattern()

    override fun addFirst(argument: Term): Pattern = super.addFirst(argument).castToPattern()

    override fun insertAt(index: Int, argument: Term): Pattern = super.insertAt(index, argument).castToPattern()

    override fun copyWithTags(tags: Map<String, Any>): Term = PatternImpl(functor, args, tags)

    override fun setArgs(vararg args: Term): Pattern = Pattern.of(functor, *args)

    override fun setArgs(args: Iterable<Term>): Pattern = Pattern.of(functor, args)

    override fun setArgs(args: Sequence<Term>): Pattern = Pattern.of(functor, args)

    override fun append(argument: Term): Pattern = super.append(argument).castToPattern()

    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term =
        Pattern.of(this.functor, this.args.map { it.apply(unifier) }).setTags(tags)

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitPattern(this)
}
