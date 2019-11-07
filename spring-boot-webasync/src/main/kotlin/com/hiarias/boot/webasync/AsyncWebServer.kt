package com.hiarias.boot.webasync

import io.ktor.server.cio.CIOApplicationEngine
import org.springframework.boot.web.server.WebServer
import java.util.concurrent.TimeUnit

class AsyncWebServer(
    private val engine: CIOApplicationEngine
) : WebServer {
    override fun start() {
        engine.start(wait = true)
    }

    override fun stop() {
        engine.stop(3000, 5000, TimeUnit.MILLISECONDS)
    }

    override fun getPort(): Int {
        return engine.environment.connectors.first().port
    }
}
