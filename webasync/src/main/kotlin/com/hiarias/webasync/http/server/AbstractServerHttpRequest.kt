package com.hiarias.webasync.http.server

import org.apache.commons.logging.Log
import org.springframework.http.HttpCookie
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpLogging
import org.springframework.http.server.RequestPath
import org.springframework.util.*
import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URLDecoder
import java.util.regex.Pattern

abstract class AbstractServerHttpRequest(
    private val uri: URI,
    contextPath: String?,
    headers: HttpHeaders
) : ServerHttpRequest {

    protected val logger: Log = HttpLogging.forLogName(this::class.java)

    override val path: RequestPath = RequestPath.parse(uri, contextPath)

    private val headers: HttpHeaders = HttpHeaders.readOnlyHttpHeaders(headers)

    override val id: String = initId() ?: ObjectUtils.getIdentityHexString(this)

    override val queryParams: MultiValueMap<String, String> =
        CollectionUtils.unmodifiableMultiValueMap(initQueryParams())

    override val cookies: MultiValueMap<String, HttpCookie> = CollectionUtils.unmodifiableMultiValueMap(initCookies())

    override fun getURI(): URI = this.uri

    override fun getHeaders() = this.headers

    protected open fun initId(): String? = null

    protected open fun initQueryParams(): MultiValueMap<String, String> =
        LinkedMultiValueMap<String, String>().apply {
            val query = uri.rawQuery
            if (query != null) {
                val matcher = QUERY_PATTERN.matcher(query)
                while (matcher.find()) {
                    val name = decodeQueryParam(matcher.group(1))
                    val eq = matcher.group(2)
                    var value = matcher.group(3)
                    when {
                        value != null -> value = decodeQueryParam(value)
                        StringUtils.hasLength(eq) -> value = ""
                        else -> value = null
                    }
                    this.add(name, value)
                }
            }
        }


    protected abstract fun initCookies(): MultiValueMap<String, HttpCookie>

    @Suppress("DEPRECATION")
    private fun decodeQueryParam(value: String) =
        try {
            URLDecoder.decode(value, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            if (logger.isWarnEnabled) {
                logger.warn("$logPrefix Could not decode query value [$value]as 'UTF-8'. Falling back on default encoding: ${e.message}")
            }
            URLDecoder.decode(value)
        }

    internal val logPrefix = "[$id]"

    companion object {
        private val QUERY_PATTERN: Pattern = Pattern.compile("([^&=]+)(=?)([^&]+)?")
    }
}