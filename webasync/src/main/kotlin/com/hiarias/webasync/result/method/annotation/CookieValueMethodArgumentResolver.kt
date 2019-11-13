package com.hiarias.webasync.result.method.annotation

import io.ktor.application.ApplicationCall
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.core.MethodParameter
import org.springframework.http.HttpCookie
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.server.ServerWebInputException

class CookieValueMethodArgumentResolver(
    factory: ConfigurableBeanFactory?
) : AbstractNamedValueArgumentResolver(factory) {
    override fun createNamedValueInfo(parameter: MethodParameter): AbstractNamedValueArgumentResolver.Companion.NamedValueInfo {
        val ann = parameter.getParameterAnnotation(CookieValue::class.java) ?: throw IllegalArgumentException("No CookieValue annotation")

        return CookieValueNamedValueInfo(ann)
    }

    override suspend fun resolveName(name: String, parameter: MethodParameter, applicationCall: ApplicationCall): Any? {
        val value = applicationCall.request.cookies[name] ?: return null
        if (HttpCookie::class.java.isAssignableFrom(parameter.nestedParameterType)) {
            return HttpCookie(name, value)
        }

        return value
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return checkAnnotatedParam(parameter, CookieValue::class.java) { _, _ ->
            true
        }
    }

    override fun handleMissingValue(name: String, parameter: MethodParameter) {
        val type = parameter.nestedParameterType.simpleName
        val reason = "Missing cookie '$name' for method parameter of type $type"
        throw ServerWebInputException(reason, parameter)
    }

    companion object {
        private class CookieValueNamedValueInfo constructor(annotation: CookieValue) :
            AbstractNamedValueArgumentResolver.Companion.NamedValueInfo(annotation.name, annotation.required, annotation.defaultValue)
    }
}
