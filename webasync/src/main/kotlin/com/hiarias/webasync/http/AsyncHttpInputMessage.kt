package com.hiarias.webasync.http

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpMessage

interface AsyncHttpInputMessage : HttpMessage {
    suspend fun getBody(): DataBuffer
}
