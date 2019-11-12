package com.hiarias.boot.webasync.autoconfigure

import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.ApplicationEngineFactory
import io.ktor.util.KtorExperimentalAPI
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.jackson.JacksonProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

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
    fun applicationEngineFactory(): ApplicationEngineFactory<CIOApplicationEngine, CIOApplicationEngine.Configuration> {
        return CIO
    }
}