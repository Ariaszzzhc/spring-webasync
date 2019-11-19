package com.hiarias.webasync.filter

import io.ktor.application.ApplicationCall

class DefaultWebFilterChain(
    val filters: List<WebFilter>,
    private val currentFilter: WebFilter? = null,
    private val chain: DefaultWebFilterChain? = null
) : WebFilterChain {

    constructor(vararg filters: WebFilter) : this(filters.toList())

    override suspend fun filter(call: ApplicationCall) {
        if (this.currentFilter != null && this.chain != null) {
            invokeFilter(this.currentFilter, this.chain, call)
        }
    }

    private suspend fun invokeFilter(current: WebFilter, chain: DefaultWebFilterChain, call: ApplicationCall) {
        return current.filter(call, chain)
    }

    companion object {
        private fun initChain(filters: List<WebFilter>): DefaultWebFilterChain {
            var chain = DefaultWebFilterChain(filters)
            val iterator = filters.listIterator()
            while (iterator.hasPrevious()) {
                chain = DefaultWebFilterChain(filters, iterator.previous(), chain)
            }

            return chain
        }
    }
}
