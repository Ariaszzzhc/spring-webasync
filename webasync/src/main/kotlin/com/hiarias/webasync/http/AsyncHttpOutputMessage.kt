package com.hiarias.webasync.http

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.http.HttpMessage

interface AsyncHttpOutputMessage : HttpMessage {
    val dataBufferFactory: DataBufferFactory

    suspend fun writeWith(body: DataBuffer)
}
