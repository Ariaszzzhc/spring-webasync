package com.hiarias.webasync.http

import com.hiarias.webasync.http.server.ServerHttpRequest

interface HttpHandler {
    suspend fun handle(request: ServerHttpRequest, response: ServerHttpResponse)
}
