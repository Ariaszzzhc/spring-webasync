package com.hiarias.webasync.http.server

import io.ktor.features.origin
import io.ktor.http.parseClientCookiesHeader
import io.ktor.request.*
import io.ktor.util.InternalAPI
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.toMap
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpCookie
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpLogging
import org.springframework.http.server.RequestPath
import org.springframework.util.CollectionUtils.unmodifiableMultiValueMap
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.util.ObjectUtils
import java.io.UnsupportedEncodingException
import java.net.InetSocketAddress
import java.net.URI
import java.net.URISyntaxException
import java.net.URLDecoder


class AsyncServerHttpRequest(
    private val request: ApplicationRequest,
    private val factory: DefaultDataBufferFactory
) : ServerHttpRequest {

    override val remoteAddress: InetSocketAddress = InetSocketAddress(request.origin.host, request.origin.port)

    override val version: String = request.httpVersion

    override fun getMethodValue(): String = request.httpMethod.value

    private val uri: URI = URI(request.uri)

    override suspend fun getBody(): DataBuffer {
        lateinit var buffer: DefaultDataBuffer

        request.call.receiveChannel().read {
            buffer = factory.wrap(it)
        }

        return buffer
    }

    private val logger = HttpLogging.forLogName(this::class.java)

    override val path: RequestPath = RequestPath.parse(uri, "")

    @UseExperimental(InternalAPI::class)
    private val headers = request.headers

    @UseExperimental(InternalAPI::class)
    override fun getHeaders(): HttpHeaders {
        return HttpHeaders().apply {
            headers.toMap().forEach { (key, value) ->
                this.addAll(key, value)
            }
        }
    }

    override val id: String = initId() ?: ObjectUtils.getIdentityHexString(this)

    override val queryParams: MultiValueMap<String, String> = unmodifiableMultiValueMap(LinkedMultiValueMap<String, String>().apply {
        request.queryParameters.toMap().forEach { (key, value) ->
            this.addAll(key, value)
        }
    })

    override val cookies: MultiValueMap<String, HttpCookie> = unmodifiableMultiValueMap(initCookies())

    private val logPrefix: String = "[$id] "

    @UseExperimental(
        InternalAPI::class,
        KtorExperimentalAPI::class
    )
    private fun initCookies(): MultiValueMap<String, HttpCookie> {
        val cookies = LinkedMultiValueMap<String, HttpCookie>()

        val cookieHeaders = headers.getAll("Cookie") ?: return cookies

        return cookies.apply {
            cookieHeaders.map {
                parseClientCookiesHeader(it)
            }.forEach {
                it.forEach { (key, value) ->
                    this.add(key, HttpCookie(key, value))
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getNativeRequest(): T = this.request as T

    private fun decodeQueryParam(value: String): String {
        return try {
            URLDecoder.decode(value, "UTF-8");
        } catch (ex: UnsupportedEncodingException) {
            if (logger.isWarnEnabled) {
                logger.warn("${logPrefix}Could not decode query value [" + value + "] as 'UTF-8'. Falling back on default encoding: ${ex.message}")
            }
            URLDecoder.decode(value);
        }
    }

    override fun getURI(): URI = this.uri

    private fun initId(): String? {
        return null
    }
}
