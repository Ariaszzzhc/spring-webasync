package com.hiarias.boot.webasync

import com.hiarias.webasync.http.HttpHandler
import org.springframework.boot.web.server.*
import java.net.InetAddress

class AsyncWebServerFactory(
    port: Int
) : AbstractConfigurableWebServerFactory(port), ConfigurableWebServerFactory {
    fun getWebServer(handler: HttpHandler): WebServer {
        TODO("not implemented")
    }
}
