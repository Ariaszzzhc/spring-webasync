package com.hiarias.webasync.result.method.annotation

import com.hiarias.webasync.BindingContext
import io.ktor.application.ApplicationCall
import org.springframework.beans.ConversionNotSupportedException
import org.springframework.beans.TypeMismatchException
import org.springframework.beans.factory.config.BeanExpressionContext
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.core.MethodParameter
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ValueConstants
import org.springframework.web.server.ServerErrorException
import org.springframework.web.server.ServerWebInputException
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractNamedValueArgumentResolver(
    private val configurableBeanFactory: ConfigurableBeanFactory?
) : HandlerMethodArgumentResolverSupport() {

    private val expressionContext: BeanExpressionContext? = configurableBeanFactory?.let {
        BeanExpressionContext(it, null)
    }

    private val namedValueInfoCache: MutableMap<MethodParameter, NamedValueInfo> = ConcurrentHashMap(256)

    override suspend fun resolveArgument(
        parameter: MethodParameter, bindingContext: BindingContext, applicationCall: ApplicationCall
    ): Any? {

        val namedValueInfo = getNamedValueInfo(parameter)
        val nestedParameter = parameter.nestedIfOptional()

        val resolvedName = resolveStringValue(namedValueInfo.name) ?: throw
        IllegalArgumentException(
            "Specified name must not resolve to null: [${namedValueInfo.name}]"
        )

        val model = bindingContext.model

        return resolveName(resolvedName.toString(), nestedParameter, applicationCall).let {
            if (it == null) {
                getDefaultValue(namedValueInfo, parameter, bindingContext, model, applicationCall)
            } else {
                var arg = it
                if ("" == it && namedValueInfo.defaultValue != null) {
                    arg = resolveStringValue(namedValueInfo.defaultValue)
                }
                arg = applyConversion(arg, namedValueInfo, parameter, bindingContext, applicationCall)
                handleResolvedValue(arg, namedValueInfo.name, parameter, model, applicationCall)
                arg!!
            }
        }
    }

    private fun getNamedValueInfo(parameter: MethodParameter): NamedValueInfo {
        var namedValueInfo: NamedValueInfo? = this.namedValueInfoCache[parameter]
        if (namedValueInfo == null) {
            namedValueInfo = createNamedValueInfo(parameter)
            namedValueInfo = updateNamedValueInfo(parameter, namedValueInfo)
            this.namedValueInfoCache[parameter] = namedValueInfo
        }
        return namedValueInfo
    }

    protected abstract fun createNamedValueInfo(parameter: MethodParameter): NamedValueInfo

    private fun updateNamedValueInfo(parameter: MethodParameter, info: NamedValueInfo): NamedValueInfo {
        var name: String? = info.name
        if (info.name.isEmpty()) {
            name = parameter.parameterName
            if (name == null) {
                val type = parameter.nestedParameterType.name
                throw IllegalArgumentException(
                    "Name for argument type [$type] not " +
                        "available, and parameter name information not found in class file either."
                )
            }
        }
        val defaultValue = (if (ValueConstants.DEFAULT_NONE == info.defaultValue) null else info.defaultValue)
        return NamedValueInfo(name!!, info.required, defaultValue)
    }

    private fun resolveStringValue(value: String): Any? {
        if (this.configurableBeanFactory == null || this.expressionContext == null) {
            return value
        }
        val placeholdersResolved = this.configurableBeanFactory.resolveEmbeddedValue(value)
        val exprResolver = this.configurableBeanFactory.beanExpressionResolver ?: return value
        return exprResolver.evaluate(placeholdersResolved, this.expressionContext)
    }

    protected abstract suspend fun resolveName(
        name: String,
        parameter: MethodParameter,
        applicationCall: ApplicationCall
    ): Any?

    protected open fun handleResolvedValue(
        arg: Any?, name: String, parameter: MethodParameter, model: Model, applicationCall: ApplicationCall
    ) {
    }

    protected open fun handleMissingValue(name: String, parameter: MethodParameter, applicationCall: ApplicationCall) {
        handleMissingValue(name, parameter)
    }

    protected open fun handleMissingValue(name: String, parameter: MethodParameter) {
        val typeName = parameter.nestedParameterType.simpleName
        throw ServerWebInputException(
            "Missing argument '$name' for method parameter of type $typeName", parameter
        )
    }

    private fun handleNullValue(value: Any?, paramType: Class<*>): Any? {
        if (value == null) {
            if (Boolean::class.java == paramType) {
                return false
            }
        }
        return value
    }

    private fun getDefaultValue(
        namedValueInfo: NamedValueInfo, parameter: MethodParameter,
        bindingContext: BindingContext, model: Model, applicationCall: ApplicationCall
    ): Any? {
        var value: Any? = null
        if (namedValueInfo.defaultValue != null) {
            value = resolveStringValue(namedValueInfo.defaultValue)
        } else if (namedValueInfo.required && !parameter.isOptional) {
            handleMissingValue(namedValueInfo.name, parameter, applicationCall)
        }
        value = handleNullValue(value, parameter.nestedParameterType)
        value = applyConversion(value, namedValueInfo, parameter, bindingContext, applicationCall)
        handleResolvedValue(value, namedValueInfo.name, parameter, model, applicationCall)
        return value
    }

    private fun applyConversion(
        value: Any?, namedValueInfo: NamedValueInfo, parameter: MethodParameter,
        bindingContext: BindingContext, applicationCall: ApplicationCall
    ): Any? {
        var ret = value

        val binder = bindingContext.createDataBinder(applicationCall, namedValueInfo.name)
        try {
            ret = binder.convertIfNecessary(ret, parameter.parameterType, parameter)
        } catch (ex: ConversionNotSupportedException) {
            throw ServerErrorException("Conversion not supported.", parameter, ex)
        } catch (ex: TypeMismatchException) {
            throw ServerWebInputException("Type mismatch.", parameter, ex)
        }

        return ret
    }

    companion object {
        open class NamedValueInfo(val name: String, val required: Boolean, val defaultValue: String?)
    }
}
