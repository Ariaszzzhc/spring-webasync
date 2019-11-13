package com.hiarias.webasync.result.method.annotation

import com.hiarias.webasync.ApplicationCallDataBinder
import com.hiarias.webasync.BindingContext
import io.ktor.application.ApplicationCall
import org.springframework.web.bind.support.SessionStatus
import org.springframework.web.bind.support.SimpleSessionStatus
import org.springframework.web.bind.support.WebBindingInitializer

class InitBinderBindingContext(
    initializer: WebBindingInitializer?,
    private val binderMethods: List<Any>
) : BindingContext(initializer) {
    private val binderMethodContext = BindingContext(initializer)

    private val sessionStatus: SessionStatus = SimpleSessionStatus()

    override fun initDataBinder(
        binder: ApplicationCallDataBinder,
        applicationCall: ApplicationCall
    ): ApplicationCallDataBinder {
        TODO("not implement yet")
    }
}
