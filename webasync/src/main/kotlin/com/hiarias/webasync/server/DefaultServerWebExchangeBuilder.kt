package com.hiarias.webasync.server

import com.hiarias.webasync.http.server.ServerHttpRequest
import com.hiarias.webasync.http.ServerHttpResponse
import kotlinx.coroutines.Deferred
import java.security.Principal

class DefaultServerWebExchangeBuilder(
    private val delegate: ServerWebExchange
) : ServerWebExchange.Builder {

    private lateinit var request: ServerHttpRequest

    private lateinit var response: ServerHttpResponse

    private lateinit var principal: Deferred<Principal>


    override fun request(requestBuilderConsumer: (ServerHttpRequest.Builder) -> Unit): ServerWebExchange.Builder {
        val builder = delegate.request.mutate()
        requestBuilderConsumer.invoke(builder)
        return request(builder.build())
    }

    override fun request(request: ServerHttpRequest): ServerWebExchange.Builder {
        this.request = request
        return this
    }

    override fun response(response: ServerHttpResponse): ServerWebExchange.Builder {
        this.response = response
        return this
    }

    override fun principal(principal: Deferred<Principal>): ServerWebExchange.Builder {
        this.principal = principal
        return this
    }

    override fun build(): ServerWebExchange = MutativeDecorator(delegate, request, response, principal)

    companion object {
        class MutativeDecorator(
            delegate: ServerWebExchange,
            request: ServerHttpRequest?,
            response: ServerHttpResponse?,
            principal: Deferred<Principal>?
        ) : ServerWebExchangeDecorator(delegate) {

            override val request = request ?: delegate.request

            override val response = response ?: delegate.response

            override val principal = principal ?: delegate.principal

        }
    }
}
