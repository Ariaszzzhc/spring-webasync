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
): List<Any?> {
    val ret = arrayListOf<Any?>()

    for ((index, param) in method.parameters.withIndex()) {
        if (param.type == Continuation::class.java) {
            continue
        }

        val mp = MethodParameter(method, index)
        mp.initParameterNameDiscovery(parameterNameDiscoverer)

        for (resolver in argumentResolvers) {
            if (resolver.supportsParameter(mp)) {
                try {
                    ret[index] = resolver.resolveArgument(mp, bindingContext, call)
                } catch (e: IndexOutOfBoundsException) {
                    ret.add(index, resolver.resolveArgument(mp, bindingContext, call))
                }
            }
        }
    }

    return ret
}
