package it.unibo.tuprolog.collections.rete.nodes.custom.leaf

import it.unibo.tuprolog.collections.rete.nodes.custom.IndexedClause
import it.unibo.tuprolog.collections.rete.nodes.custom.IndexingLeaf
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.utils.addFirst
import it.unibo.tuprolog.utils.dequeOf

internal class NumericIndex(
    private val ordered: Boolean,
    private val nestingLevel: Int
) : IndexingLeaf {

    private val index: MutableMap<Numeric, MutableList<IndexedClause>> = mutableMapOf()
    private val numerics: MutableList<IndexedClause> = dequeOf()

    override fun get(clause: Clause): Sequence<Clause> {
        return if (clause.firstParameter().isNumber)
            index[clause.asInnerNumeric()]
                ?.asSequence()
                ?.filter { it.innerClause matches clause }
                ?.map { it.innerClause }
                ?: emptySequence()
        else extractGlobalSequence(clause)
    }

    override fun assertA(clause: IndexedClause) {
        if(ordered) {
            clause.asInnerNumeric().let {
                index.getOrPut(it){ dequeOf() }
                    .addFirst(clause)
            }
            numerics.addFirst(clause)
        } else {
            assertZ(clause)
        }
    }

    override fun assertZ(clause: IndexedClause) {
        clause.asInnerNumeric().let {
            index.getOrPut(it){ dequeOf() }
                .add(clause)
        }
        numerics.add(clause)
    }

    override fun retractFirst(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun getFirst(clause: Clause): IndexedClause? {
        if (clause.firstParameter().isNumber) {
            index[clause.asInnerNumeric()].let {
                return if(it == null) null
                else extractFirst(clause, it)
            }
        }
        else {
            return extractFirst(clause, numerics)
        }
    }

    private fun extractFirst(clause: Clause, index: MutableList<IndexedClause>): IndexedClause? {
        val actualIndex = index.indexOfFirst { it.innerClause matches clause }

        return if (actualIndex == -1) null
        else index[actualIndex]
    }

    override fun getAny(clause: Clause): IndexedClause? =
        this.getFirst(clause)

    override fun getIndexed(clause: Clause): Sequence<IndexedClause> {
        return if (clause.firstParameter().isNumber)
            index[clause.asNumeric()]
                ?.asSequence()
                ?.filter { it.innerClause matches clause }
                ?: emptySequence()
        else extractGlobalIndexedSequence(clause)
    }

    override fun retractIndexed(indexed: IndexedClause): Sequence<Clause> {
        index[indexed.asInnerNumeric()]!!.remove(indexed)
        return sequenceOf(indexed.innerClause)
    }

    override fun retractAllIndexed(clause: Clause): Sequence<IndexedClause> {

        return if (clause.firstParameter().isNumber){
            val partialIndex = index.getOrElse(clause.asNumeric()){ mutableListOf() }
            return retractFromMutableList(clause, partialIndex)
        }
        else {
            retractFromMutableList(clause, numerics)
        }
    }

    private fun retractFromMutableList(clause: Clause, index: MutableList<IndexedClause>): Sequence<IndexedClause> {
        val result = index.filter { it.innerClause matches clause }
        result.forEach { index.remove(it) }
        return result.asSequence()
    }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        retractAllIndexed(clause).map { it.innerClause }

    private fun extractGlobalIndexedSequence(clause: Clause): Sequence<IndexedClause> =
        numerics.asSequence()
            .filter { it.innerClause matches clause }

    private fun extractGlobalSequence(clause: Clause): Sequence<Clause> =
        extractGlobalIndexedSequence(clause)
            .map { it.innerClause }

    private fun Clause.firstParameter(): Term =
        this.args[0]

    private fun Term.asNumeric(): Numeric =
        this as Numeric

    private fun Clause.asInnerNumeric(): Numeric =
        this as Numeric

    private fun IndexedClause.asInnerNumeric(): Numeric =
        this.innerClause.firstParameter() as Numeric

}