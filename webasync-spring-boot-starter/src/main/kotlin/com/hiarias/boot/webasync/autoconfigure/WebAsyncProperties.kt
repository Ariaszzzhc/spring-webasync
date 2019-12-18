package com.hiarias.boot.webasync.autoconfigure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.webasync")
class WebAsyncProperties {
    var host: String = "0.0.0.0"
    var port: Int = 8080
}
