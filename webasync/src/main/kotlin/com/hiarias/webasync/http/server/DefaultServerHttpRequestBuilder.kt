package com.hiarias.webasync.http.server

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpCookie
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.server.RequestPath
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.util.ObjectUtils
import java.net.URI
import java.util.*


class DefaultServerHttpRequestBuilder(
    private val original: ServerHttpRequest
) : ServerHttpRequest.Builder {
    private var uri = original.uri

    private val httpHeaders = HttpHeaders.writableHttpHeaders(original.headers)

    private var httpMethodValue = original.methodValue

    private val cookies: MultiValueMap<String, HttpCookie>

    private var uriPath: String? = null

    private var contextPath: String? = null

    private val bodyFunction: Any = original::getBody

    init {
        this.cookies = LinkedMultiValueMap(original.cookies.size)
        copyMultiValueMap(original.cookies, this.cookies)
    }


    override fun method(httpMethod: HttpMethod): ServerHttpRequest.Builder {
        this.httpMethodValue = httpMethod.name
        return this
    }

    override fun uri(uri: URI): ServerHttpRequest.Builder {
        this.uri = uri
        return this
    }

    override fun path(path: String): ServerHttpRequest.Builder {
        this.uriPath = path
        return this
    }

    override fun contextPath(contextPath: String): ServerHttpRequest.Builder {
        this.contextPath = contextPath
        return this
    }

    override fun header(headerName: String, vararg headerValues: String): ServerHttpRequest.Builder {
        this.httpHeaders[headerName] = headerValues.toList()
        return this
    }

    override fun headers(headersConsumer: (HttpHeaders) -> Unit): ServerHttpRequest.Builder {
        headersConsumer.invoke(this.httpHeaders)
        return this
    }

    @Suppress("UNCHECKED_CAST")
    override fun build(): ServerHttpRequest {
        return MutatedServerHttpRequest(
            uri,
            contextPath ?: "",
            httpHeaders,
            httpMethodValue,
            cookies,
            bodyFunction as suspend () -> DataBuffer,
            original
        )
    }

    companion object {
        private fun <K, V> copyMultiValueMap(source: MultiValueMap<K, V>, target: MultiValueMap<K, V>) {
            source.forEach { (key, value) -> target[key] = LinkedList(value) }
        }

        class MutatedServerHttpRequest(
            private val uri: URI,
            contextPath: String,
            private val headers: HttpHeaders,
            private val methodValue: String,
            override val cookies: MultiValueMap<String, HttpCookie>,
            private val bodyFunction: suspend () -> DataBuffer,
            original: ServerHttpRequest
        ) : ServerHttpRequest {
            override val id: String = ObjectUtils.getIdentityHexString(this)
            override val path: RequestPath = RequestPath.parse(uri, contextPath)

            override val queryParams = original.queryParams
            override val remoteAddress = original.remoteAddress

            override val version = original.version

            override fun getHeaders(): HttpHeaders {
                return headers
            }

            override fun getMethodValue(): String {
                return methodValue
            }

            override fun getURI(): URI {
                return uri
            }

            override suspend fun getBody() = bodyFunction.invoke()
        }
    }
}
