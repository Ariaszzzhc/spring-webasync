package com.hiarias.webasync.server

interface WebFilterChain {
    suspend fun filter(exchange: ServerWebExchange)
}
