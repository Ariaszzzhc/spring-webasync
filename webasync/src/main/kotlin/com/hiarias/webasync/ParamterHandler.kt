package com.hiarias.webasync

import com.hiarias.webasync.result.method.HandlerMethodArgumentResolver
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.util.pipeline.PipelineContext
import org.springframework.core.MethodParameter
import org.springframework.core.ParameterNameDiscoverer
import java.lang.reflect.Method
import kotlin.coroutines.Continuation

suspend fun PipelineContext<Unit, ApplicationCall>.handleParameter(
    method: Method,
    parameterNameDiscoverer: ParameterNameDiscoverer,
    bindingContext: BindingContext,
    argumentResolvers: List<HandlerMethodArgumentResolver>
) {
    method.parameters.forEachIndexed { index, parameter ->
        val value = when (parameter.type) {
            Continuation::class.java -> null
            else -> {
                var result: Any? = null
                val methodParameter = MethodParameter(method, index)
                methodParameter.initParameterNameDiscovery(parameterNameDiscoverer)
                //TODO cache resolver
                for (resolver in argumentResolvers) {
                    if (resolver.supportsParameter(methodParameter)) {
                        result = resolver.resolveArgument(methodParameter, bindingContext, call)
                    }
                }
                result
            }
        }
    }
}
