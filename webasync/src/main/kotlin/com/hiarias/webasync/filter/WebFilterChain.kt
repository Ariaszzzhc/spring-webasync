package com.hiarias.webasync.filter

import io.ktor.application.ApplicationCall

interface WebFilterChain {
    suspend fun filter(call: ApplicationCall)
}
