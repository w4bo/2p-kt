package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.PatternImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

interface Pattern : Composed {
    override val isGround: Boolean
        get() = false

    override val isPattern: Boolean
        get() = true

    override fun asPattern(): Pattern = this

    override val functor: Var

    override val indicator: Indicator
        get() = Indicator.of(functor, Integer.of(arity))

    override fun setArgs(vararg args: Term): Pattern

    override fun setArgs(args: Iterable<Term>): Pattern

    override fun setArgs(args: Sequence<Term>): Pattern

    override fun append(argument: Term): Pattern

    override fun addLast(argument: Term): Pattern

    override fun addFirst(argument: Term): Pattern

    override fun insertAt(index: Int, argument: Term): Pattern

    companion object {
        @JsName("of")
        @JvmStatic
        fun of(functor: Var, vararg arguments: Term): Pattern = of(functor, listOf(*arguments))

        @JsName("ofList")
        @JvmStatic
        fun of(functor: Var, arguments: KtList<Term>): Pattern = PatternImpl(functor, arguments)

        @JsName("ofIterable")
        @JvmStatic
        fun of(functor: Var, arguments: Iterable<Term>): Pattern = of(functor, arguments.toList())

        @JsName("ofSequence")
        @JvmStatic
        fun of(functor: Var, arguments: Sequence<Term>): Pattern = of(functor, arguments.toList())
    }
}
