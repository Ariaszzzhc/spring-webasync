package com.hiarias.webasync.filter

import io.ktor.application.ApplicationCall
import com.hiarias.webasync.filter.WebFilterChain


interface WebFilter {
    suspend fun filter(call: ApplicationCall, chain: WebFilterChain)
}
