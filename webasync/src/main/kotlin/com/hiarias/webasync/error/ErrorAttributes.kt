package com.hiarias.webasync.error

import io.ktor.application.ApplicationCall
import io.ktor.request.ApplicationRequest


interface ErrorAttributes {

    fun getErrorAttributes(error: Throwable, includeStackTrace: Boolean): Map<String, Any?>

//    fun getError(request: ApplicationRequest): Throwable
//
//    fun storeErrorInformation(error: Throwable, call: ApplicationCall)
}
