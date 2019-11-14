package com.hiarias.webasync.result.method.annotation

import io.ktor.application.ApplicationCall
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.core.MethodParameter
import org.springframework.ui.Model
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ValueConstants
import org.springframework.web.server.ServerErrorException

class PathVariableMethodArgumentResolver(
    factory: ConfigurableBeanFactory?
) : AbstractNamedValueArgumentResolver(factory) {
    override fun createNamedValueInfo(parameter: MethodParameter): AbstractNamedValueArgumentResolver.Companion.NamedValueInfo {
        val ann = parameter.getParameterAnnotation(PathVariable::class.java) ?: throw IllegalArgumentException("No PathVariable annotation")
        return PathVariableNamedValueInfo(ann)
    }

    override suspend fun resolveName(name: String, parameter: MethodParameter, applicationCall: ApplicationCall): Any? {
        return applicationCall.parameters[name]
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return checkAnnotatedParam(parameter, PathVariable::class.java, ::singlePathVariable)
    }

    private fun singlePathVariable(pathVariable: PathVariable, type: Class<*>): Boolean {
        return !Map::class.java.isAssignableFrom(type) || StringUtils.hasText(pathVariable.name)
    }

    override fun handleMissingValue(name: String, parameter: MethodParameter) {
        throw ServerErrorException(name, parameter, null)
    }

    override fun handleResolvedValue(
        arg: Any?,
        name: String,
        parameter: MethodParameter,
        model: Model,
        applicationCall: ApplicationCall
    ) {
        //TODO
    }

    companion object {
        private class PathVariableNamedValueInfo(annotation: PathVariable) :
            AbstractNamedValueArgumentResolver.Companion.NamedValueInfo(annotation.name, annotation.required, ValueConstants.DEFAULT_NONE)
    }
}
