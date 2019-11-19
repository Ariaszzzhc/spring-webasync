package com.hiarias.webasync.error

import io.ktor.util.AttributeKey
import org.springframework.core.annotation.MergedAnnotation
import org.springframework.core.annotation.MergedAnnotations
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ResponseStatusException
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Date
import kotlin.reflect.jvm.jvmName

class DefaultErrorAttributes(
    private val includeException: Boolean = false
) : ErrorAttributes {
    override fun getErrorAttributes(error: Throwable, includeStackTrace: Boolean): MutableMap<String, Any?> {
//        val error = getError(request)
        val responseStatusAnnotation = MergedAnnotations
            .from(error.javaClass, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)
            .get(ResponseStatus::class.java)

        val errorAttributes = mutableMapOf<String, Any?>().apply {
            put("timestamp", Date())
//            put("path", request.path())
            val errorStatus = determineHttpStatus(error, responseStatusAnnotation)
            put("status", errorStatus.value())
            put("error", errorStatus.reasonPhrase)
            put("message", determineMessage(error, responseStatusAnnotation))
//            put("requestId", request.call.callId)
        }

        handleException(errorAttributes, determineException(error), includeStackTrace)

        return errorAttributes
    }

//    override fun getError(request: ApplicationRequest): Throwable {
//        return request.call.attributes.getOrNull(ERROR_ATTRIBUTE) ?: throw IllegalStateException("Missing exception attribute in ApplicationRequest")
//    }
//
//    override fun storeErrorInformation(error: Throwable, call: ApplicationCall) {
//        call.attributes.put(ERROR_ATTRIBUTE, error)
//    }

    private fun addStackTrace(errorAttributes: MutableMap<String, Any?>, error: Throwable) {
        val stackTrace = StringWriter()
        error.printStackTrace(PrintWriter(stackTrace))
        stackTrace.flush()
        errorAttributes["trace"] = stackTrace.toString()
    }

    private fun handleException(
        errorAttributes: MutableMap<String, Any?>,
        error: Throwable,
        includeStackTrace: Boolean
    ) {
        if (this.includeException) {
            errorAttributes["exception"] = error.javaClass.name
        }
        if (includeStackTrace) {
            addStackTrace(errorAttributes, error)
        }
        if (error is BindingResult) {
            val result = error as BindingResult
            if (result.hasErrors()) {
                errorAttributes["errors"] = result.allErrors
            }
        }
    }

    private fun determineHttpStatus(
        error: Throwable,
        responseStatusAnnotation: MergedAnnotation<ResponseStatus>
    ): HttpStatus {
        return if (error is ResponseStatusException) {
            error.status
        } else responseStatusAnnotation.getValue(
            "code",
            HttpStatus::class.java
        ).orElse(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun determineMessage(
        error: Throwable,
        responseStatusAnnotation: MergedAnnotation<ResponseStatus>
    ): String? {
        if (error is WebExchangeBindException) {
            return error.message!!
        }
        return if (error is ResponseStatusException) {
            error.reason
        } else responseStatusAnnotation.getValue(
            "reason",
            String::class.java
        ).orElseGet {
            error.message
        }
    }

    private fun determineException(error: Throwable): Throwable {
        return if (error is ResponseStatusException) {
            if (error.cause != null) error.cause!! else error
        } else error
    }

    companion object {
        val ERROR_ATTRIBUTE = AttributeKey<Throwable>("${DefaultErrorAttributes::class.jvmName}.ERROR")
    }
}
