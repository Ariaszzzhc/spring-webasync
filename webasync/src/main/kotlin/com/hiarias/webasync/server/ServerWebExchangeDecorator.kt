package com.hiarias.webasync.server

import java.security.Principal
import java.time.Instant

open class ServerWebExchangeDecorator(
    private val delegate: ServerWebExchange
) : ServerWebExchange {

    override val request = delegate.request

    override val response = delegate.response

    override val attributes = delegate.attributes

    override suspend fun getSession() = delegate.getSession()

    override suspend fun <T : Principal> getPrincipal() = delegate.getPrincipal<T>()

    override suspend fun getFormData() = delegate.getFormData()

    override suspend fun getMultipartData() = delegate.getMultipartData()

    override val localeContext = delegate.localeContext

    override val applicationContext = delegate.applicationContext

    override val isNotModified = delegate.isNotModified

    override fun checkNotModified(lastModified: Instant) = delegate.checkNotModified(lastModified)

    override fun checkNotModified(etag: String) = delegate.checkNotModified(etag)

    override fun checkNotModified(etag: String?, lastModified: Instant) = delegate.checkNotModified(etag, lastModified)

    override fun transformUrl(url: String) = delegate.transformUrl(url)

    override fun addUrlTransformer(transformer: (String) -> String) = delegate.addUrlTransformer(transformer)

    override val logPrefix = delegate.logPrefix

    override fun toString(): String = "${this::class.java.simpleName}[delegate=$delegate]"
}