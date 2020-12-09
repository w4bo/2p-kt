package it.unibo.tuprolog.solve.libs.io

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asPromise
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.promise
import kotlin.coroutines.CoroutineContext

internal actual fun <T> awaitBlocking(
    context: CoroutineContext,
    block: suspend CoroutineScope.() -> T
): T {
    val deferred = GlobalScope.async { block() }.asPromise()
    deferred.
    return deferred.getCompleted()
}