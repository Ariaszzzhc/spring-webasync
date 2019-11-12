package com.hiarias.boot.webasync.autoconfigure

import com.hiarias.webasync.RouteResolver
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.ApplicationEngineFactory
import io.ktor.server.engine.embeddedServer
import io.ktor.sessions.Sessions
import org.springframework.beans.BeanUtils
import org.springframework.boot.autoconfigure.jackson.JacksonProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.util.ClassUtils

abstract class AbstractWebAsyncAutoConfiguration(
    private val properties: WebAsyncProperties,
    private val jacksonProperties: JacksonProperties,
    protected val context: ApplicationContext
) {

    @Bean
    open fun applicationEngine(
        applicationEngineFactory: ApplicationEngineFactory<*, *>,
        context: ApplicationContext
    ): ApplicationEngine {
        val resolver = RouteResolver(context)

        return embeddedServer(applicationEngineFactory, properties.port, properties.host) {
            installFeatures(jacksonProperties)

            routing {
                route("/") {
                    resolver.resolve().forEach {
                        TODO()
                    }
                }
            }

        }.start(wait = true)
    }
}

fun Application.installFeatures(jacksonProperties: JacksonProperties) {
    install(Sessions)
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
    install(CallLogging)
}

inline fun <reified T> instantiateClass(cls: String): T =
    BeanUtils.instantiateClass(ClassUtils.forName(cls, null)) as T
