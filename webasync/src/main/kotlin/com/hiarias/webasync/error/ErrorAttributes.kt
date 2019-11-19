package com.hiarias.webasync.error

interface ErrorAttributes {

    fun getErrorAttributes(error: Throwable, includeStackTrace: Boolean): Map<String, Any?>

//    fun getError(request: ApplicationRequest): Throwable
//
//    fun storeErrorInformation(error: Throwable, call: ApplicationCall)
}
