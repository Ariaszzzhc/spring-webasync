package com.hiarias.webasync.http.buffer

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import java.nio.ByteBuffer

class AsyncDataBufferFactory : DataBufferFactory {
    override fun wrap(byteBuffer: ByteBuffer): DataBuffer {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun wrap(bytes: ByteArray): DataBuffer {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun allocateBuffer(): DataBuffer {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun allocateBuffer(initialCapacity: Int): DataBuffer {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun join(dataBuffers: MutableList<out DataBuffer>): DataBuffer {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
