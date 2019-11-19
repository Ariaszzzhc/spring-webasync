package com.hiarias.webasync.result.method.annotation

import io.ktor.application.ApplicationCall
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.RequestHeader

class RequestHeaderMethodArgumentResolver(
    factory: ConfigurableBeanFactory?
) : AbstractNamedValueArgumentResolver(factory) {
    override fun createNamedValueInfo(parameter: MethodParameter): AbstractNamedValueArgumentResolver.Companion.NamedValueInfo {
        val ann = parameter.getParameterAnnotation(RequestHeader::class.java)
            ?: throw IllegalStateException("No RequestHeader annotation")

        return RequestHeaderNamedValueInfo(ann)
    }

    override suspend fun resolveName(name: String, parameter: MethodParameter, applicationCall: ApplicationCall): Any? {
        val headers = applicationCall.request.headers.getAll(name)
        return if (headers != null && headers.size == 1) {
            headers[0]
        } else {
            headers
        }
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return checkAnnotatedParam(parameter, RequestHeader::class.java, ::singleParam)
    }

    private fun singleParam(annotation: RequestHeader, type: Class<*>): Boolean {
        return !Map::class.java.isAssignableFrom(type)
    }

    companion object {
        private class RequestHeaderNamedValueInfo constructor(annotation: RequestHeader) :
            AbstractNamedValueArgumentResolver.Companion.NamedValueInfo(
                annotation.name,
                annotation.required,
                annotation.defaultValue
            )
    }
}
