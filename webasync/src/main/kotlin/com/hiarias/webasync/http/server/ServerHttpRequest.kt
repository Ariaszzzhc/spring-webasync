package com.hiarias.webasync.http.server

import com.hiarias.webasync.http.AsyncHttpInputMessage
import org.springframework.http.HttpCookie
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpRequest
import org.springframework.http.server.RequestPath
import org.springframework.util.MultiValueMap
import java.net.InetSocketAddress
import java.net.URI

interface ServerHttpRequest : HttpRequest, AsyncHttpInputMessage {

    val id: String

    val path: RequestPath

    val queryParams: MultiValueMap<String, String>

    val cookies: MultiValueMap<String, HttpCookie>

    val remoteAddress: InetSocketAddress

    val version: String

//    fun mutate(): ServerHttpRequest.Builder {
//        return DefaultServerHttpRequestBuilder(this)
//    }
//
//

    interface Builder {
        fun method(httpMethod: HttpMethod): Builder

        fun uri(uri: URI): Builder

        fun path(path: String): Builder

        fun contextPath(contextPath: String): Builder

        fun header(headerName: String, vararg headerValues: String): Builder

        fun headers(headersConsumer: (HttpHeaders) -> Unit): Builder

        fun build(): ServerHttpRequest
    }
}