package it.unibo.tuprolog.solve.libs.io

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

internal actual fun <T> awaitBlocking(
    context: CoroutineContext,
    block: suspend CoroutineScope.() -> T
): T {
    return runBlocking(context, block)
}