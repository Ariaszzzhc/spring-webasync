package com.hiarias.webasync.http.server

import com.hiarias.webasync.http.AsyncHttpOutputMessage
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.util.MultiValueMap

interface ServerHttpResponse : AsyncHttpOutputMessage {
    var statusCode: HttpStatus
    val cookie: MultiValueMap<String, ResponseCookie>
}
