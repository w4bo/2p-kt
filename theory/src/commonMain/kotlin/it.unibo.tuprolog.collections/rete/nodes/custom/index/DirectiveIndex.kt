package it.unibo.tuprolog.collections.rete.nodes.custom.index

import it.unibo.tuprolog.collections.rete.nodes.custom.IndexedClause
import it.unibo.tuprolog.collections.rete.nodes.custom.ReteNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator.Companion.matches

internal class DirectiveIndex(private val ordered: Boolean) : ReteNode {

    private val directives: MutableList<IndexedClause> = dequeOf()

    override fun get(clause: Clause): Sequence<Clause> =
        directives
            .filter { it.innerClause matches clause }
            .map { it.innerClause }
            .asSequence()

    override fun assertA(clause: IndexedClause) {
        if(ordered) directives.addFirst(clause)
        else assertZ(clause)
    }

    override fun assertZ(clause: IndexedClause) {
        directives.add(clause)
    }

    override fun retractFirst(clause: Clause): Sequence<Clause> {
        directives.indexOfFirst { it.innerClause matches clause }.let {
            return when(it){
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

}