package com.hiarias.webasync.http.server

import com.hiarias.webasync.http.buffer.AsyncDataBufferFactory
import io.ktor.features.origin
import io.ktor.http.parseClientCookiesHeader
import io.ktor.request.ApplicationRequest
import io.ktor.request.httpMethod
import io.ktor.request.httpVersion
import io.ktor.request.uri
import io.ktor.util.InternalAPI
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.toMap
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpCookie
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpLogging
import org.springframework.http.server.RequestPath
import org.springframework.util.CollectionUtils.unmodifiableMultiValueMap
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import java.io.UnsupportedEncodingException
import java.net.InetSocketAddress
import java.net.URI
import java.net.URISyntaxException
import java.net.URLDecoder
import java.util.regex.Pattern


class AsyncServerHttpRequest(
    private val request: ApplicationRequest,
    private val factory: AsyncDataBufferFactory,
    private val uri: URI,
    contextPath: String?
) : ServerHttpRequest {

    override val remoteAddress: InetSocketAddress = InetSocketAddress(request.origin.host, request.origin.port)

    override val version: String = request.httpVersion

    override fun getMethodValue(): String = request.httpMethod.value

    override suspend fun getBody(): DataBuffer {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val logger = HttpLogging.forLogName(this::class.java)

    override val path: RequestPath = RequestPath.parse(uri, contextPath)

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

    override val queryParams: MultiValueMap<String, String> = unmodifiableMultiValueMap(initQueryParams())

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

    private fun initQueryParams(): MultiValueMap<String, String> {
        val queryParams: MultiValueMap<String, String> = LinkedMultiValueMap()
        val query = getURI().rawQuery
        if (query != null) {
            val matcher = QUERY_PATTERN.matcher(query)
            while (matcher.find()) {
                val name = decodeQueryParam(matcher.group(1))
                val eq = matcher.group(2)
                var value = matcher.group(3)
                value = value?.let { decodeQueryParam(it) } ?: if (StringUtils.hasLength(eq)) "" else null
                queryParams.add(name, value)
            }
        }
        return queryParams
    }

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

    companion object {
        val QUERY_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?")!!

        private fun initUri(request: ApplicationRequest): URI {
            return URI(resolveBaseUrl(request).toString() + resolveRequestUri(request))
        }

        private fun getScheme(request: ApplicationRequest): String {
            return request.origin.scheme
        }


        private fun resolveBaseUrl(request: ApplicationRequest): URI {
            val scheme = getScheme(request)
            val header = request.headers["HOST"]

            if (header != null) {
                val portIndex = if (header.startsWith("[")) {
                    header.indexOf(':', header.indexOf(']'))
                } else {
                    header.indexOf(':')
                }

                return if (portIndex != -1) {
                    try {
                        URI(
                            scheme, null, header.substring(0, portIndex),
                            header.substring(portIndex + 1).toInt(), null, null, null
                        )
                    } catch (ex: NumberFormatException) {
                        throw URISyntaxException(header, "Unable to parse port", portIndex)
                    }
                } else {
                    URI(scheme, header, null, null)
                }
            } else {

                return URI(
                    scheme, null, request.origin.host,
                    request.origin.port, null, null, null
                )
            }
        }

        private fun resolveRequestUri(request: ApplicationRequest): String {
            val uri = request.uri
            for (i in 0 until uri.length) {
                var c = uri[i]
                if (c == '/' || c == '?' || c == '#') {
                    break
                }
                if (c == ':' && i + 2 < uri.length) {
                    if (uri[i + 1] === '/' && uri[i + 2] === '/') {
                        for (j in i + 3 until uri.length) {
                            c = uri[j]
                            if (c == '/' || c == '?' || c == '#') {
                                return uri.substring(j)
                            }
                        }
                        return ""
                    }
                }
            }
            return uri
        }
    }
}
