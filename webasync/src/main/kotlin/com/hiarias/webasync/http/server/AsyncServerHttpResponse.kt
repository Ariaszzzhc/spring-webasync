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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.io.File

class AsyncServerHttpResponse(
    private val response: ApplicationResponse,
    override val bufferFactory: DataBufferFactory
) : ServerHttpResponse, ZeroCopyHttpOutputMessage {

    override val cookies: MultiValueMap<String, ResponseCookie> = LinkedMultiValueMap()

    private val headers: HttpHeaders = HttpHeaders().apply {
        response.headers.allValues().toMap().forEach { (key, value) ->
            this.addAll(key, value)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getNativeResponse(): T {
        return this.response as T
    }

    override suspend fun writeWith(file: File) {
        response.call.respondFile(file)
    }

    override fun getHeaders() = this.headers

    override var statusCode: HttpStatus
        get() = this.response.status().let {
            HttpStatus.resolve(it!!.value)!!
        }
        set(value) {
            this.response.status(HttpStatusCode(value.value(), value.reasonPhrase))
        }

    override suspend fun writeWith(body: DataBuffer) {
        response.call.respond(body)
    }
}
