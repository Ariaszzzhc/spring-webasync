package com.hiarias.boot.webasync.autoconfigure

import io.ktor.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.ApplicationEngineFactory
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.stopServerOnCancellation
import io.ktor.util.KtorExperimentalAPI
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.jackson.JacksonProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit

@Configuration
@ConditionalOnClass(CIO::class)
@UseExperimental(KtorExperimentalAPI::class)
@EnableConfigurationProperties(WebAsyncProperties::class, JacksonProperties::class)
class CIOWebAsyncAutoConfiguration(
    webAsyncProperties: WebAsyncProperties,
    jacksonProperties: JacksonProperties,
    context: ApplicationContext
) : AbstractWebAsyncAutoConfiguration(webAsyncProperties, jacksonProperties, context) {


    @Bean
    @ConditionalOnMissingBean
    fun applicationEngineFactory(): ApplicationEngineFactory<CIOApplicationEngine, CIOApplicationEngine.Configuration> {
        return CIO
    }
}
