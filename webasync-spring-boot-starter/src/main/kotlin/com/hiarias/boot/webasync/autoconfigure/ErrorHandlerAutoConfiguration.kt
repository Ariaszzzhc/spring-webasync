package com.hiarias.boot.webasync.autoconfigure

import com.hiarias.webasync.error.DefaultErrorAttributes
import com.hiarias.webasync.error.ErrorAttributes
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.SearchStrategy
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@AutoConfigureBefore(AbstractWebAsyncAutoConfiguration::class)
@EnableConfigurationProperties(ServerProperties::class, ResourceProperties::class)
class ErrorHandlerAutoConfiguration(
    private val serverProperties: ServerProperties
) {

    @Bean
    @ConditionalOnMissingBean(value = [ErrorAttributes::class], search = SearchStrategy.CURRENT)
    fun errorAttributes(): DefaultErrorAttributes {
        return DefaultErrorAttributes(
            this.serverProperties.error.isIncludeException
        )
    }
}
