package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.libs.io.Url.Companion.toString
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import kotlin.streams.asSequence

data class JvmUrl(private val url: URL) : Url {

    constructor(string: String) : this(string.toUrl())

    constructor(protocol: String, host: String = "", port: Int? = null, path: String = "", query: String? = null) :
        this(toString(protocol, host, port, path, query))

    override val protocol: String
        get() = url.protocol

    override val host: String
        get() = url.host

    override val path: String
        get() = url.path

    override val port: Int?
        get() = url.port.let { if (it > 0) it else null }

    override val query: String?
        get() = url.query

    override suspend fun readAsText(): String =
        withContext(Dispatchers.IO) {
            try {
                @Suppress("BlockingMethodInNonBlockingContext")
                BufferedReader(InputStreamReader(url.openStream())).lines().asSequence().joinToString("\n")
            } catch (e: java.io.IOException) {
                throw IOException(e.message, e)
            }
        }

    override suspend fun readAsByteArray(): ByteArray =
        withContext(Dispatchers.IO) {
            try {
                @Suppress("BlockingMethodInNonBlockingContext")
                BufferedInputStream(url.openStream()).readAllBytes()
            } catch (e: java.io.IOException) {
                throw IOException(e.message, e)
            }
        }

    override fun toString(): String = url.toString()

    companion object {
        private fun String.toUrl(): URL =
            try {
                URL(this)
            } catch (e: MalformedURLException) {
                throw InvalidUrlException(
                    message = "Invalid URL: $this",
                    cause = e
                )
            }
    }
}
