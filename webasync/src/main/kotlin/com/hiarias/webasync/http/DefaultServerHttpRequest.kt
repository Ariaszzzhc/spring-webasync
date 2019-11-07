package com.hiarias.webasync.http

import com.hiarias.webasync.http.server.ServerHttpRequest
import kotlinx.coroutines.io.*
import kotlinx.io.core.ByteOrder
import kotlinx.io.core.ByteReadPacket
import kotlinx.io.core.ExperimentalIoApi
import kotlinx.io.core.IoBuffer
import org.springframework.http.HttpCookie
import org.springframework.http.HttpHeaders
import org.springframework.http.server.RequestPath
import org.springframework.http.server.reactive.SslInfo
import org.springframework.util.MultiValueMap
import java.net.InetSocketAddress
import java.net.URI
import java.nio.ByteBuffer

class DefaultServerHttpRequest(
    override val id: String,
    override val path: RequestPath,
    override val queryParams: MultiValueMap<String, String>,
    override val cookies: MultiValueMap<String, HttpCookie>,
    override val getRemoteAddress: InetSocketAddress?,
    override val getSslInf: SslInfo?,
    override val version: String,
    override val availableForRead: Int,
    override val isClosedForRead: Boolean,
    override val isClosedForWrite: Boolean,
    override var readByteOrder: ByteOrder,
    override val totalBytesRead: Long
) : ServerHttpRequest {
    override fun getHeaders(): HttpHeaders {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMethodValue(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getURI(): URI {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancel(cause: Throwable?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun consumeEachBufferRange(visitor: ConsumeEachBufferVisitor) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun discard(max: Long): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @ExperimentalIoApi
    override fun <R> lookAhead(visitor: LookAheadSession.() -> R): R {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @ExperimentalIoApi
    override suspend fun <R> lookAheadSuspend(visitor: suspend LookAheadSuspendSession.() -> R): R {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun read(min: Int, consumer: (ByteBuffer) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun readAvailable(dst: ByteBuffer): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun readAvailable(dst: ByteArray, offset: Int, length: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun readAvailable(dst: IoBuffer): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun readBoolean(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun readByte(): Byte {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun readDouble(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun readFloat(): Float {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun readFully(dst: ByteBuffer): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun readFully(dst: ByteArray, offset: Int, length: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun readFully(dst: IoBuffer, n: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun readInt(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun readLong(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun readPacket(size: Int, headerSizeHint: Int): ByteReadPacket {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun readRemaining(limit: Long, headerSizeHint: Int): ByteReadPacket {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @ExperimentalIoApi
    override fun readSession(consumer: ReadSession.() -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun readShort(): Short {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @ExperimentalIoApi
    override suspend fun readSuspendableSession(consumer: suspend SuspendableReadSession.() -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun readUTF8Line(limit: Int): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun <A : Appendable> readUTF8LineTo(out: A, limit: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}