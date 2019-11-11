package com.hiarias.webasync.http.server

import com.hiarias.webasync.http.AsyncHttpOutputMessage
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.util.MultiValueMap

interface ServerHttpResponse : AsyncHttpOutputMessage {
    val cookies: MultiValueMap<String, ResponseCookie>
    fun setStatusCode(status: HttpStatus?): Boolean
    fun getStatusCode(): HttpStatus?
    fun addCookie(cookie: ResponseCookie)
}
