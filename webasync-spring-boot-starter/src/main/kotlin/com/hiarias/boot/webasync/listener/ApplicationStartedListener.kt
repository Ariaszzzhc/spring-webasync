package com.hiarias.boot.webasync.listener

import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.addShutdownHook
import io.ktor.util.KtorExperimentalAPI
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import java.util.concurrent.TimeUnit

class ApplicationStartedListener(
    private val engine: ApplicationEngine
) : ApplicationListener<ApplicationReadyEvent> {
    @UseExperimental(KtorExperimentalAPI::class)
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        engine.addShutdownHook {
            engine.stop(3, 5, TimeUnit.SECONDS)
        }
        engine.start(true)
    }
}
