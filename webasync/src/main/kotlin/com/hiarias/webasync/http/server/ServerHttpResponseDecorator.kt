package com.hiarias.webasync.http.server

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus

class ServerHttpResponseDecorator(
    private val delegate: ServerHttpResponse
) : ServerHttpResponse {
    override var statusCode: HttpStatus
        get() = delegate.statusCode
        set(value) {
            delegate.statusCode = value
        }

    override val cookie = delegate.cookie

    override val bufferFactory = delegate.bufferFactory

    override suspend fun writeWith(body: DataBuffer) {
        delegate.writeWith(body)
    }

    override fun getHeaders() = delegate.headers
}
