package com.hiarias.webasync.http.server

class ServerHttpRequestDecorator(
    private val delegate: ServerHttpRequest
) : ServerHttpRequest {
    override val id = delegate.id

    override val path = delegate.path

    override val queryParams = delegate.queryParams

    override val cookies = delegate.cookies

    override val remoteAddress = delegate.remoteAddress

    override val version = delegate.version

    override fun getHeaders() = delegate.headers

    override fun getMethodValue() = delegate.methodValue

    override fun getURI() = delegate.uri

    override suspend fun getBody() = delegate.getBody()

    override fun toString() = "${this::class.java.simpleName} [delegate=$delegate]"
}
