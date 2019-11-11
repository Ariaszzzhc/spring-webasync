package com.hiarias.webasync.http.server

import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.util.CollectionUtils
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.util.concurrent.atomic.AtomicReference
import org.springframework.core.io.buffer.DataBuffer


abstract class AbstractServerHttpResponse(
    override val dataBufferFactory: DataBufferFactory,
    private val headers: HttpHeaders = HttpHeaders()
) : ServerHttpResponse {

    override val cookies: MultiValueMap<String, ResponseCookie> = LinkedMultiValueMap()
        get() {
            return if (this.state.get() == State.COMMITTED) {
                CollectionUtils.unmodifiableMultiValueMap(field)
            } else {
                field
            }
        }

    private var statusCodeValue: Int? = null

    private val state: AtomicReference<State> = AtomicReference(State.NEW)

    private enum class State { NEW, COMMITTING, COMMITTED }

    override fun getHeaders(): HttpHeaders {
        return if (this.state.get() == State.COMMITTED) {
            HttpHeaders.readOnlyHttpHeaders(this.headers)
        } else {
            this.headers
        }
    }

    override fun setStatusCode(status: HttpStatus?): Boolean {
        return if (this.state.get() == State.COMMITTED) {
            false
        } else {
            this.statusCodeValue = status?.value()
            true
        }
    }

    override fun getStatusCode(): HttpStatus? {
        return if (this.statusCodeValue != null) {
            HttpStatus.resolve(this.statusCodeValue!!)
        } else {
            null
        }
    }

    override fun addCookie(cookie: ResponseCookie) {
        if (this.state.get() === State.COMMITTED) {
            throw IllegalStateException(
                "Can't add the cookie " + cookie +
                        "because the HTTP response has already been committed"
            )
        } else {
            cookies.add(cookie.name, cookie)
        }
    }

    override suspend fun writeWith(body: DataBuffer) {
        if (!this.state.compareAndSet(State.NEW, State.COMMITTING)) {
            return
        }
        applyCookies()
        applyHeaders()
        applyStatusCode()
        try {
            writeWithInternal(body)
        } catch(e: Throwable) {
            removeContentLength()
        } finally {
            this.state.set(State.COMMITTED)
        }
    }

    protected abstract suspend fun writeWithInternal(body: DataBuffer)

    protected abstract fun applyStatusCode()

    protected abstract fun applyHeaders()

    protected abstract fun applyCookies()

    abstract fun <T> getNativeResponse(): T

    fun isCommitted(): Boolean {
        return this.state.get() !== State.NEW
    }

    private fun removeContentLength() {
        if (!this.isCommitted()) {
            this.getHeaders().remove(HttpHeaders.CONTENT_LENGTH)
        }
    }
}