package com.hiarias.webasync.server

interface WebFilter {
    suspend fun filter(exchange: ServerWebExchange, chain: WebFilterChain)
}
