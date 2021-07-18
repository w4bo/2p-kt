package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.utils.setTags

internal abstract class AbstractStruct(
    override val functor: String,
    override val args: List<Term>,
    tags: Map<String, Any> = emptyMap()
) : AbstractComposed(functor, args, tags), Struct {

    override val isGround: Boolean
        get() = checkGroundness()

    protected open fun checkGroundness(): Boolean = variables.none()

    override fun freshCopy(): Struct = super.freshCopy().castToStruct()

    override fun freshCopy(scope: Scope): Struct = when {
        isGround -> this
        else -> scope.structOf(functor, argsSequence.map { it.freshCopy(scope) }).setTags(tags)
    }

    final override fun structurallyEquals(other: Term): Boolean =
        other.isStruct && other.castToStruct().let {
            functor == it.functor && arity == it.arity && itemsAreStructurallyEqual(it)
        }

    override val isFunctorWellFormed: Boolean
        get() = Struct.isWellFormedFunctor(functor)

    override val functorToString: String
        get() {
            val escaped = Struct.escapeFunctorIfNecessary(functor)
            return Struct.enquoteFunctorIfNecessary(escaped)
        }

    override fun append(argument: Term): Struct = super.append(argument).castToStruct()

    override fun addLast(argument: Term): Struct = super.addLast(argument).castToStruct()

    override fun addFirst(argument: Term): Struct = super.addFirst(argument).castToStruct()

    override fun insertAt(index: Int, argument: Term): Struct = super.insertAt(index, argument).castToStruct()

    override fun setArgs(vararg args: Term): Struct = Struct.of(functor, *args)

    override fun setArgs(args: Iterable<Term>): Struct = Struct.of(functor, args)

    override fun setArgs(args: Sequence<Term>): Struct = Struct.of(functor, args)

    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term =
        Struct.of(this.functor, this.args.map { it.apply(unifier) }).setTags(tags)

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitStruct(this)
}
