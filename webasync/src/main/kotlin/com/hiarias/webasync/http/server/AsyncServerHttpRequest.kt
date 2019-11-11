package com.hiarias.webasync.http.server

import io.ktor.http.parseClientCookiesHeader
import io.ktor.request.ApplicationRequest
import io.ktor.request.httpMethod
import io.ktor.request.httpVersion
import io.ktor.request.uri
import io.ktor.util.InternalAPI
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.toMap
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpCookie
import org.springframework.http.HttpHeaders
import org.springframework.http.server.RequestPath
import org.springframework.util.CollectionUtils.unmodifiableMultiValueMap
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.util.ObjectUtils
import java.io.UnsupportedEncodingException
import java.net.InetSocketAddress
import java.net.URI
import java.net.URLDecoder


@UseExperimental(InternalAPI::class)
class AsyncServerHttpRequest(
    private val request: ApplicationRequest,
    private val factory: DefaultDataBufferFactory
) : AbstractServerHttpRequest(initUri(request), "", initHeaders(request)) {

    override val remoteAddress: InetSocketAddress = InetSocketAddress(uri.host, uri.port)

    override val version: String = request.httpVersion

    override fun getMethodValue(): String = request.httpMethod.value

    override suspend fun getBody(): DataBuffer {
        lateinit var buffer: DefaultDataBuffer

        request.receiveChannel().read {
            buffer = factory.wrap(it)
        }

        return buffer
    }

    override val path: RequestPath = RequestPath.parse(uri, "")

    override val id: String = initId() ?: ObjectUtils.getIdentityHexString(this)

    override val queryParams: MultiValueMap<String, String> = unmodifiableMultiValueMap(LinkedMultiValueMap<String, String>().apply {
        request.queryParameters.toMap().forEach { (key, value) ->
            this.addAll(key, value)
        }
    })

    override val cookies: MultiValueMap<String, HttpCookie> = unmodifiableMultiValueMap(initCookies())

    @UseExperimental(
        InternalAPI::class,
        KtorExperimentalAPI::class
    )
    override fun initCookies(): MultiValueMap<String, HttpCookie> {
        val cookies = LinkedMultiValueMap<String, HttpCookie>()

        val cookieHeaders = headers["Cookie"] ?: return cookies

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

    @Suppress("DEPRECATION")
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

    companion object {
        private fun initUri(request: ApplicationRequest) = URI(request.uri)

//        private fun resolveBaseUrl(request: Request): URI {
//            val scheme = "http"
//            val host = request.headers["Host"]?.toString()
//
//            if (host != null) {
//                val portIndex = if (host.startsWith("[")) {
//                    host.indexOf(':', host.indexOf(']'))
//                } else {
//                    host.indexOf(':')
//                }
//
//                return if (portIndex != -1) {
//                    try {
//                        URI(
//                            scheme, null, host.substring(0, portIndex),
//                            Integer.parseInt(host.substring(portIndex + 1)), null, null, null
//                        )
//                    } catch (ex: NumberFormatException) {
//                        throw URISyntaxException(host, "Unable to parse port", portIndex)
//                    }
//
//                } else {
//                    URI(scheme, host, null, null)
//                }
//            } else {
//                return URI(scheme, null, "localhost",
//                    80, null, null, null)
//            }
//        }

//        private fun resolveRequestUri(request: Request): String {
//            val uri = request.uri.toString()
//            for (i in 0 until uri.length) {
//                var c = uri[i]
//                if (c == '/' || c == '?' || c == '#') {
//                    break
//                }
//                if (c == ':' && i + 2 < uri.length) {
//                    if (uri[i + 1] == '/' && uri[i + 2] == '/') {
//                        for (j in i + 3 until uri.length) {
//                            c = uri[j]
//                            if (c == '/' || c == '?' || c == '#') {
//                                return uri.substring(j)
//                            }
//                        }
//                        return ""
//                    }
//                }
//            }
//            return uri
//        }

        private fun initHeaders(request: ApplicationRequest) = HttpHeaders().apply {
            request.headers.toMap().forEach { (key, value) ->
                this.addAll(key, value)
            }
        }
    }
}
