package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentInputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrChar
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.peekCharAndReply
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object PeekChar1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("peek_char") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsVarOrChar(0)
        return peekCharAndReply(currentInputChannel, first)
    }
}
