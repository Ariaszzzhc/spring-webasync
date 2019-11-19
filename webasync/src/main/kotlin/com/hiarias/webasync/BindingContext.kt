package com.hiarias.webasync

import io.ktor.application.ApplicationCall
import org.springframework.ui.Model
import org.springframework.validation.support.BindingAwareConcurrentModel
import org.springframework.web.bind.support.WebBindingInitializer

open class BindingContext(private val initializer: WebBindingInitializer? = null) {

    val model: Model = BindingAwareConcurrentModel()

    fun createDataBinder(applicationCall: ApplicationCall, target: Any?, name: String): ApplicationCallDataBinder {
        val dataBinder = ApplicationCallDataBinder(target, name)
        if (this.initializer != null) {
            this.initializer.initBinder(dataBinder)
        }

        return initDataBinder(dataBinder, applicationCall)
    }

    protected open fun initDataBinder(
        binder: ApplicationCallDataBinder,
        applicationCall: ApplicationCall
    ): ApplicationCallDataBinder {
        return binder
    }

    fun createDataBinder(applicationCall: ApplicationCall, name: String): ApplicationCallDataBinder {
        return createDataBinder(applicationCall, null, name)
    }
}
