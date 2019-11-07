package com.hiarias.webasync.server

import com.hiarias.webasync.http.server.ServerHttpRequest
import com.hiarias.webasync.http.ServerHttpResponse
import org.springframework.context.ApplicationContext
import org.springframework.context.i18n.LocaleContext
import org.springframework.http.codec.multipart.Part
import org.springframework.util.MultiValueMap
import java.security.Principal
import java.time.Instant

interface ServerWebExchange {
    val request: ServerHttpRequest

    val response: ServerHttpResponse

    val attributes: Map<String, Any>

    @Suppress("UNCHECKED_CAST")
    fun <T> getAttribute(name: String) = attributes[name] as T?

    fun <T> getRequiredAttribute(name: String) =
        getAttribute<T>(name) ?: throw IllegalArgumentException("Required attribute '$name' is missing.")

    @Suppress("UNCHECKED_CAST")
    fun <T> getAttributeOrDefault(name: String, defaultValue: T): T =
        attributes.getOrDefault(name, defaultValue) as T

    suspend fun getSession(): WebSession

    suspend fun <T : Principal> getPrincipal(): T

    suspend fun getFormData(): MultiValueMap<String, String>

    suspend fun getMultipartData(): MultiValueMap<String, Part>

    val localeContext: LocaleContext

    val applicationContext: ApplicationContext?

    val isNotModified: Boolean

    fun checkNotModified(lastModified: Instant): Boolean

    fun checkNotModified(etag: String): Boolean

    fun checkNotModified(etag: String?, lastModified: Instant): Boolean

    fun transformUrl(url: String): String

    fun addUrlTransformer(transformer: (String) -> String)

    val logPrefix: String

//    fun mutate(): Builder {
//        return DefaultServerWebExchangeBuilder(this)
//    }

    interface Builder {
        fun request(requestBuilderConsumer: (ServerHttpRequest.Builder) -> Unit): Builder

        fun request(request: ServerHttpRequest): Builder

        fun response(response: ServerHttpResponse): Builder

        fun principal(principalCoroutine: suspend () -> Principal): Builder

        fun build(): ServerWebExchange
    }


    companion object {
        val LOG_ID_ATTRIBUTE = "${ServerWebExchange::class.java.name}.LOG_ID"
    }
}