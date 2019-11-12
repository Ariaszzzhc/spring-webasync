package com.hiarias.boot.webasync.autoconfigure

import io.ktor.server.engine.ApplicationEngineFactory
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.jackson.JacksonProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnClass(Netty::class)
@EnableConfigurationProperties(WebAsyncProperties::class, JacksonProperties::class)
class NettyWebAsyncAutoConfiguration(
    properties: WebAsyncProperties,
    jacksonProperties: JacksonProperties,
    context: ApplicationContext
) : AbstractWebAsyncAutoConfiguration(properties, jacksonProperties, context) {

    @Bean
    fun applicationEngineFactory(): ApplicationEngineFactory<NettyApplicationEngine, NettyApplicationEngine.Configuration> {
        return Netty
    }
}