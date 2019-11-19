package com.hiarias.webasync.filter

import io.ktor.application.ApplicationCall

interface WebFilter {
    suspend fun filter(call: ApplicationCall, chain: WebFilterChain)
}
