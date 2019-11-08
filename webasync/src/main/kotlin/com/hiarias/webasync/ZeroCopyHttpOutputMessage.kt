package com.hiarias.webasync

import com.hiarias.webasync.http.AsyncHttpOutputMessage
import java.io.File
import java.nio.file.Path

interface ZeroCopyHttpOutputMessage : AsyncHttpOutputMessage {
    suspend fun writeWith(file: File)
}
