package com.hiarias.webasync.result.method.annotation

import io.ktor.application.ApplicationCall
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.core.MethodParameter
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ValueConstants
import org.springframework.web.server.ServerWebInputException

class RequestParamMethodArgumentResolver(
    configurableBeanFactory: ConfigurableBeanFactory?,
    private val useDefaultResolution: Boolean
) : AbstractNamedValueArgumentResolver(configurableBeanFactory) {
    override fun createNamedValueInfo(parameter: MethodParameter): AbstractNamedValueArgumentResolver.Companion.NamedValueInfo {
        val ann = parameter.getParameterAnnotation(RequestParam::class.java)
        return if(ann != null) {
            RequestParamNamedValueInfo(ann)
        } else {
            RequestParamNamedValueInfo()
        }
    }

    override suspend fun resolveName(name: String, parameter: MethodParameter, applicationCall: ApplicationCall): Any? {
        val params = applicationCall.request.queryParameters.getAll(name)
        return if (params != null && params.size == 1) {
            params[0]
        } else {
            params
        }
    }

    override fun handleMissingValue(name: String, parameter: MethodParameter) {
        val type = parameter.nestedParameterType.simpleName
        val reason = "Required $type parameter '$name' is not present"
        throw ServerWebInputException(reason, parameter)
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        if (checkAnnotatedParam(parameter, RequestParam::class.java, ::singleParam)) {
            return true
        }


        return when {
            checkAnnotatedParam(parameter, RequestParam::class.java, ::singleParam) -> true
            this.useDefaultResolution -> checkParameterType(parameter, BeanUtils::isSimpleProperty) or BeanUtils.isSimpleProperty(parameter.nestedIfOptional().nestedParameterType)
            else -> false
        }
    }

    private fun singleParam(requestParam: RequestParam, type: Class<*>): Boolean {
        return (!Map::class.java.isAssignableFrom(type) or StringUtils.hasText(requestParam.name))
    }

    companion object {
        private class RequestParamNamedValueInfo : AbstractNamedValueArgumentResolver.Companion.NamedValueInfo {

            constructor() : super("", true, ValueConstants.DEFAULT_NONE)

            constructor(annotation: RequestParam) : super(
                annotation.name,
                annotation.required,
                annotation.defaultValue
            )
        }
    }
}
