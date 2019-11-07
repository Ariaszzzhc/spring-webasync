package com.hiarias.webasync.server

interface WebHandler {
    suspend fun handle(exchange: ServerWebExchange)
}
