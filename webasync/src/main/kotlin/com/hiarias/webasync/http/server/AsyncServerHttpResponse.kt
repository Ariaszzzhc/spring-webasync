package com.hiarias.webasync.http.server

import com.hiarias.webasync.ZeroCopyHttpOutputMessage
import io.ktor.http.HttpStatusCode
import io.ktor.response.ApplicationResponse
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.util.toMap
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.http.HttpHeaders
import java.io.File

class AsyncServerHttpResponse(
    private val response: ApplicationResponse,
    dataBufferFactory: DataBufferFactory
) : AbstractServerHttpResponse(dataBufferFactory, initHeaders(response)), ZeroCopyHttpOutputMessage {
    override suspend fun writeWithInternal(body: DataBuffer) {
        this.response.call.respond(body)
    }

    override fun applyStatusCode() {
        val statusCode = getStatusCode()
        if (statusCode != null) {
            this.response.status(HttpStatusCode.fromValue(statusCode.value()))
        }
    }

    override fun applyHeaders() {
//        this.headers.forEach { (key, value) ->
//            value.forEach {
//                this.response.headers.append(key, it)
//            }
//        }
    }

    override fun applyCookies() {
        this.cookies.forEach { (key, value) ->
            value.forEach {
                this.response.cookies.append(key, it.value)
            }
        }
    }

    override suspend fun writeWith(file: File) {
        this.response.call.respondFile(file)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getNativeResponse(): T {
        return this.response as T
    }

    companion object {
        fun initHeaders(response: ApplicationResponse): HttpHeaders = HttpHeaders().apply {
            response.headers.allValues().toMap().forEach { (key, value) ->
                this.addAll(key, value)
            }
        }
    }
}
