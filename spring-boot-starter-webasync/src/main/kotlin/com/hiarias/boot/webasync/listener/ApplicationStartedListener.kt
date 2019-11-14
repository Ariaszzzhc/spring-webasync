package com.hiarias.boot.webasync.listener

import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.addShutdownHook
import io.ktor.util.KtorExperimentalAPI
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.ContextStartedEvent
import java.util.concurrent.TimeUnit

class ApplicationStartedListener(
    private val engine: ApplicationEngine
) : ApplicationListener<ContextStartedEvent> {
    @UseExperimental(KtorExperimentalAPI::class)
    override fun onApplicationEvent(event: ContextStartedEvent) {
        engine.addShutdownHook {
            engine.stop(3, 5, TimeUnit.SECONDS)
        }
        engine.start(true)
    }
}
