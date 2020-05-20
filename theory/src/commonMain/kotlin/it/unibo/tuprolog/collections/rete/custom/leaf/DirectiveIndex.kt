package it.unibo.tuprolog.collections.rete.custom.leaf

import it.unibo.tuprolog.collections.rete.custom.Retractable
import it.unibo.tuprolog.collections.rete.custom.TopLevelReteNode
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.utils.addFirst
import it.unibo.tuprolog.utils.dequeOf

internal class DirectiveIndex(private val ordered: Boolean) : TopLevelReteNode {

    private val directives: MutableList<IndexedClause> = dequeOf()

    override fun get(clause: Clause): Sequence<Clause> =
        directives
            .filter { it.innerClause matches clause }
            .map { it.innerClause }
            .asSequence()

    override fun assertA(clause: IndexedClause) {
        if (ordered) directives.addFirst(clause)
        else assertZ(clause)
    }

    override fun assertZ(clause: IndexedClause) {
        directives.add(clause)
    }

    override fun retractFirst(clause: Clause): Sequence<Clause> {
        directives.indexOfFirst { it.innerClause matches clause }.let {
            return when (it) {
                -1 -> emptySequence()
                else -> {
                    val result = directives[it]
                    directives.removeAt(it)
                    sequenceOf(result.innerClause)
                }
            }
        }
    }

    override fun retractAll(clause: Clause): Sequence<Clause> {
        val result = directives.filter { it.innerClause matches clause }
        result.forEach { directives.remove(it) }
        return result.asSequence().map { it.innerClause }
    }

    override fun getCache(): Sequence<SituatedIndexedClause> =
        directives.asSequence().map {
            SituatedIndexedClause.of(it, object : Retractable {
                override fun retractIndexed(indexed: SituatedIndexedClause) {
                    TODO(
                        "Directives are adapted as a SituatedIndexedClause, but they are not actually not stored " +
                                "with this type. Given their particular semantic, try retracting directly with a proper " +
                                "query, or opening a pull request implementing this indexing class as a proper typed data " +
                                "structure"
                    )
                }

            })
        }

}