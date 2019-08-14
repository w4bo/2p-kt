package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.Signature

/** A class representing a solution to a goal */
sealed class Solution {

    /** The query to which the solution refers */
    abstract val query: Struct
    /** The substitution that has been applied to find the solution, or a `failed` substitution */
    abstract val substitution: Substitution
    /** The Struct representing the solution, or `null` if no solution is available */
    abstract val solvedQuery: Struct?

    /** A class representing the successful solution */
    data class Yes(
            override val query: Struct,
            /** The successful substitution applied finding the solution */
            override val substitution: Substitution.Unifier
    ) : Solution() {

        constructor(signature: Signature, arguments: List<Term>, substitution: Substitution.Unifier)
                : this(Struct.of(signature.name, arguments), substitution) {

            arityAndArgumentsCountCheck(signature, arguments)
        }

        /** The Struct representing the solution */
        override val solvedQuery: Struct by lazy { substitution.applyTo(query) as Struct }
    }

    /** A class representing a failed solution */
    data class No(override val query: Struct) : Solution() {

        constructor(signature: Signature, arguments: List<Term>) : this(Struct.of(signature.name, arguments)) {
            arityAndArgumentsCountCheck(signature, arguments)
        }

        /** Always [Substitution.Fail] because no solution was found */
        override val substitution: Substitution.Fail = Substitution.failed()
        /** Always `null` because no solution was found */
        override val solvedQuery: Struct? = null
    }

    /** Utility function to check correctness on Solution construction */
    protected fun arityAndArgumentsCountCheck(signature: Signature, arguments: List<Term>) =
            require(signature.arity == arguments.count()) {
                "Trying to create Solution.Yes of signature `$signature` with wrong number of arguments $arguments"
            }
}