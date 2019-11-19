package com.hiarias.webasync.result.method

import com.hiarias.webasync.BindingContext
import io.ktor.application.ApplicationCall
import org.springframework.core.MethodParameter

interface HandlerMethodArgumentResolver {
    fun supportsParameter(parameter: MethodParameter): Boolean

    suspend fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        applicationCall: ApplicationCall
    ): Any?
}
