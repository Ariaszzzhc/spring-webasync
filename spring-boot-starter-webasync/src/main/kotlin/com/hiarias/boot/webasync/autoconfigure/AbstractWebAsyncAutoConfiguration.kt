package com.hiarias.boot.webasync.autoconfigure

import com.hiarias.boot.webasync.listener.ApplicationStartedListener
import com.hiarias.webasync.RouteResolver
import com.hiarias.webasync.error.DefaultErrorAttributes
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.header
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.ApplicationEngineFactory
import io.ktor.server.engine.embeddedServer
import io.ktor.sessions.Sessions
import kotlinx.atomicfu.atomic
import org.slf4j.event.Level
import org.springframework.beans.BeanUtils
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.jackson.JacksonProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.util.ClassUtils

abstract class AbstractWebAsyncAutoConfiguration(
    private val properties: WebAsyncProperties,
    private val jacksonProperties: JacksonProperties,
    protected val context: ApplicationContext
) {

    @Bean
    @ConditionalOnMissingBean
    open fun applicationEngine(
        applicationEngineFactory: ApplicationEngineFactory<*, *>,
        context: ApplicationContext,
        errorAttributes: DefaultErrorAttributes
    ): ApplicationEngine {
        val resolver = RouteResolver(context as ConfigurableApplicationContext)

        return embeddedServer(applicationEngineFactory, properties.port, properties.host) {
            installFeatures(jacksonProperties, errorAttributes)
            routing {
                route("/") {
                    resolver.resolve(this)
                }
            }

        }
    }

    @Bean
    @ConditionalOnMissingBean
    open fun applicationStartedListener(applicationEngine: ApplicationEngine): ApplicationStartedListener {
        return ApplicationStartedListener(applicationEngine)
    }
}

fun Application.installFeatures(jacksonProperties: JacksonProperties, errorAttributes: DefaultErrorAttributes) {
    install(CallId) {
        retrieve {
            it.request.header(HttpHeaders.XRequestId)
        }

        val counter = atomic(0)
        generate { "generated-call-id-${counter.getAndIncrement()}" }

        verify { callId: String ->
            callId.isNotEmpty()
        }
    }

    install(Sessions)
    install(StatusPages) {
        exception<Throwable> { cause ->
            val res = errorAttributes.getErrorAttributes(cause, false)
            res["path"] = this.call.request.path()
            res["callId"] = this.call.callId
            call.respond(HttpStatusCode.fromValue(res["status"] as Int), res)
        }
    }

    install(ContentNegotiation) {
        jackson {
            if (jacksonProperties.dateFormat != null) {
                this.dateFormat = instantiateClass(jacksonProperties.dateFormat)
            }

            if (jacksonProperties.propertyNamingStrategy != null) {
                this.propertyNamingStrategy = instantiateClass(jacksonProperties.propertyNamingStrategy)
            }

            if (jacksonProperties.locale != null) {
                this.setLocale(jacksonProperties.locale)
            }

            if (jacksonProperties.timeZone != null) {
                this.setTimeZone(jacksonProperties.timeZone)
            }

        }
    }
    install(CallLogging) {
        level = Level.INFO
        callIdMdc("mdc-call-id")
    }
}

inline fun <reified T> instantiateClass(cls: String): T =
    BeanUtils.instantiateClass(ClassUtils.forName(cls, null)) as T
