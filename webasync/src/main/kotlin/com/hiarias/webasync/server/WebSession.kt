package com.hiarias.webasync.server

import java.time.Duration
import java.time.Instant

interface WebSession {
    var id: String

    val attributes: Map<String, Any>

    @Suppress("UNCHECKED_CAST")
    fun <T> getAttribute(name: String): T? = attributes[name] as T?

    fun <T> getRequiredAttribute(name: String): T =
        getAttribute<T>(name) ?: throw IllegalArgumentException("Required attribute '$name' is missing.")

    @Suppress("UNCHECKED_CAST")
    fun <T> getAttributeOrDefault(name: String, defaultValue: T): T = attributes.getOrDefault(name, defaultValue) as T

    fun start()

    val isStarted: Boolean

    suspend fun changeSessionId()

    suspend fun invalidate()

    suspend fun save()

    val isExpired: Boolean

    val creationTime: Instant

    val lastAccessTime: Instant

    fun setMaxIdleTime(maxIdleTime: Duration)

    fun getMaxIdleTime(): Duration
}
