package com.hiarias.webasync.server

import com.hiarias.webasync.http.server.ServerHttpRequest
import com.hiarias.webasync.http.ServerHttpResponse
import java.security.Principal

class DefaultServerWebExchangeBuilder(
    private val delegate: ServerWebExchange
) : ServerWebExchange.Builder {
    override fun request(requestBuilderConsumer: (ServerHttpRequest.Builder) -> Unit): ServerWebExchange.Builder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun request(request: ServerHttpRequest): ServerWebExchange.Builder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun response(response: ServerHttpResponse): ServerWebExchange.Builder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun principal(principalCoroutine: suspend () -> Principal): ServerWebExchange.Builder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build(): ServerWebExchange {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
