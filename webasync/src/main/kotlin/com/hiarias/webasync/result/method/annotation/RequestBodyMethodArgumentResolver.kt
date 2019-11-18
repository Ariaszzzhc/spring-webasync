package com.hiarias.webasync.result.method.annotation

import com.hiarias.webasync.BindingContext
import io.ktor.application.ApplicationCall
import io.ktor.request.receiveOrNull
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.server.ServerWebInputException

class RequestBodyMethodArgumentResolver : HandlerMethodArgumentResolverSupport() {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return checkAnnotatedParam(parameter, RequestBody::class.java) { _, _ ->
            true
        }
    }

    override suspend fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        applicationCall: ApplicationCall
    ): Any? {
        try {
            return applicationCall.receiveOrNull(parameter.parameterType.kotlin)
        } catch (e: Throwable) {
            throw ServerWebInputException("Failed to read HTTP message", parameter, e)
        }
    }
}
