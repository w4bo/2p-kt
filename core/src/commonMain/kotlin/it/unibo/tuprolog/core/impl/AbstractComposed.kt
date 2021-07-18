package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Composed
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.itemWiseHashCode

@Suppress("EqualsOrHashCode")
internal abstract class AbstractComposed(
    override val functor: Any,
    override val args: List<Term>,
    tags: Map<String, Any> = emptyMap()
) : TermImpl(tags), Composed {

    override fun freshCopy(): Composed = freshCopy(Scope.empty())

    abstract override fun freshCopy(scope: Scope): Composed

    @Suppress("RedundantAsSequence")
    protected open fun itemsAreStructurallyEqual(other: Composed): Boolean {
        for (i in 0 until arity) {
            if (!getArgAt(i).structurallyEquals(other[i])) {
                return false
            }
        }
        return true
    }

    final override fun equals(other: Any?): Boolean =
        asTerm(other)?.asComposed()?.let { equalsImpl(it, true) } ?: false

    @Suppress("RedundantAsSequence")
    protected open fun itemsAreEqual(other: Composed, useVarCompleteName: Boolean): Boolean {
        for (i in 0 until arity) {
            if (!getArgAt(i).equals(other[i], useVarCompleteName)) {
                return false
            }
        }
        return true
    }

    final override fun equals(other: Term, useVarCompleteName: Boolean): Boolean =
        other.asComposed()?.let { equalsImpl(it, useVarCompleteName) } ?: false

    private fun equalsImpl(other: Composed, useVarCompleteName: Boolean): Boolean {
        if (this === other) return true
        if (functor != other.functor) return false
        if (arity != other.arity) return false
        if (!itemsAreEqual(other, useVarCompleteName)) return false
        return true
    }

    override val hashCodeCache: Int by lazy {
        var result = functor.hashCode()
        result = 31 * result + arity
        result = 31 * result + argsHashCode()
        result
    }

    protected open fun argsHashCode(): Int = itemWiseHashCode(args)

    override fun toString(): String =
        "$functorToString${if (arity > 0) "(${args.joinToString(", ")})" else ""}"

    protected abstract val functorToString: String

    override fun append(argument: Term): Composed = addLast(argument)

    override fun addLast(argument: Term): Composed = setArgs(args + argument)

    override fun addFirst(argument: Term): Composed = setArgs(listOf(argument) + args)

    override fun insertAt(index: Int, argument: Term): Composed =
        if (index in 0 until arity) {
            val argsArray = args.toTypedArray()
            setArgs(
                *argsArray.sliceArray(0 until index),
                argument,
                *argsArray.sliceArray(index until arity)
            )
        } else {
            throw IndexOutOfBoundsException("Index $index is out of bounds ${args.indices}")
        }

    override fun setFunctor(functor: Var): Pattern = Pattern.of(functor, args)

    override fun setFunctor(functor: String): Struct = Struct.of(functor, args)

    override val variables: Sequence<Var>
        get() = argsSequence.flatMap { it.variables }
}
