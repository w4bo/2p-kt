package it.unibo.tuprolog.core

import kotlin.collections.List
import kotlin.js.JsName

interface Composed : Term {

    override val isComposed: Boolean
        get() = true

    override fun asComposed(): Composed = this

    /**
     * The functor of this [Composed].
     */
    @JsName("functor")
    val functor: Any

    /**
     * The total amount of arguments of this [Composed].
     * This is equal to the length of [args].
     */
    @JsName("arity")
    val arity: Int
        get() = args.size

    /**
     * The indicator corresponding to this [Composed], i.e. [functor]/[arity].
     */
    @JsName("indicator")
    val indicator: Indicator

    /**
     * List of arguments of this [Composed].
     */
    @JsName("args")
    val args: List<Term>

    /**
     * Sequence of arguments of this [Composed].
     */
    @JsName("argsSequence")
    val argsSequence: Sequence<Term>
        get() = args.asSequence()

    /**
     * Gets the [index]-th argument if this [Composed].
     * @param index is the index the argument which should be retrieved
     * @throws IndexOutOfBoundsException if [index] is lower than 0 or greater or equal to [arity]
     * @return the [Term] having position [index] in [args]
     */
    @JsName("getArgAt")
    fun getArgAt(index: Int): Term = args[index]

    @JsName("setArgs")
    fun setArgs(vararg args: Term): Composed

    @JsName("setArgsIterable")
    fun setArgs(args: Iterable<Term>): Composed

    @JsName("setArgsSequence")
    fun setArgs(args: Sequence<Term>): Composed

    /**
     * Alias for [getArgAt].
     * In Kotlin, this method enables the syntax `compound[index]`.
     */
    @JsName("get")
    operator fun get(index: Int): Term = getArgAt(index)

    /**
     * Creates a novel [Struct] which is a copy of the current [Composed], expect that is has a different functor.
     * @param functor is a [String] representing the new functor
     * @return a new [Struct], whose functor is [functor], and whose [arity] and arguments list are equal
     * to the current one
     */
    @JsName("setFunctor")
    fun setFunctor(functor: String): Struct

    /**
     * Creates a novel [Pattern] which is a copy of the current [Composed], expect that is has a different functor.
     * @param functor is a [Var] representing the new functor
     * @return a new [Pattern], whose functor is [functor], and whose [arity] and arguments list are equal
     * to the current one
     */
    @JsName("setFunctorVar")
    fun setFunctor(functor: Var): Pattern

    /**
     * An alias for [addLast].
     */
    @JsName("append")
    fun append(argument: Term): Composed = addLast(argument)

    /**
     * Creates a novel [Composed] which is a copy of the current one, expect that is has one more argument.
     * The novel [argument] is appended at the end of the new [Composed]'s arguments list.
     * @param argument is a [Term] of any sort
     * @return a new [Composed], whose [functor] is equals to the current one,
     * whose [arity] is greater than the current one, and whose last argument is [argument]
     */
    @JsName("addLast")
    fun addLast(argument: Term): Composed

    /**
     * Creates a novel [Composed] which is a copy of the current one, expect that is has one more argument.
     * The novel [argument] is appended at the beginning of the new [Composed]'s arguments list.
     * @param argument is a [Term] of any sort
     * @return a new [Composed], whose [functor] is equals to the current one,
     * whose [arity] is greater than the current one, and whose first argument is [argument]
     */
    @JsName("addFirst")
    fun addFirst(argument: Term): Composed

    /**
     * Creates a novel [Composed] which is a copy of the current one, expect that is has one more argument.
     * The novel [argument] is inserted into the new [Composed]'s arguments list, at index [index], wheres subsequent
     * arguments indexes are shifted by 1.
     * @param index is the index the new [argument] should be inserted into
     * @param argument is a [Term] of any sort
     * @throws IndexOutOfBoundsException if [index] is lower than 0 or greater or equal to [arity]
     * @return a new [Composed], whose [functor] is equals to the current one,
     * whose [arity] is greater than the current one, and whose [index]-th argument is [argument]
     */
    @JsName("insertAt")
    fun insertAt(index: Int, argument: Term): Composed
}
