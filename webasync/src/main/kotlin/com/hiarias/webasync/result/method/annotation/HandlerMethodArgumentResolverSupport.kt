package com.hiarias.webasync.result.method.annotation

import com.hiarias.webasync.result.method.HandlerMethodArgumentResolver
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.core.MethodParameter

abstract class HandlerMethodArgumentResolverSupport : HandlerMethodArgumentResolver {
    protected val logger: Log = LogFactory.getLog(javaClass)

    protected fun checkParameterType(parameter: MethodParameter, block: (Class<*>) -> Boolean): Boolean {
        return block.invoke(parameter.parameterType)
    }

    protected fun <A : Annotation> checkAnnotatedParam(
        parameter: MethodParameter, annotationType: Class<A>, block: (A, Class<*>) -> Boolean
    ): Boolean {
        val annotation = parameter.getParameterAnnotation(annotationType) ?: return false
        return block.invoke(annotation, parameter.nestedIfOptional().nestedParameterType)
    }
}
