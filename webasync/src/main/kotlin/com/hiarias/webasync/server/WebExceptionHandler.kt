package com.hiarias.webasync.server

interface WebExceptionHandler {
    suspend fun handle(exchange: ServerWebExchange, ex: Throwable)
}
