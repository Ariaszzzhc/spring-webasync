package com.hiarias.webasync

import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.routing.Route
import io.ktor.routing.route
import org.springframework.core.LocalVariableTableParameterNameDiscoverer

fun Route.register(definition: RouteDefinition) {
    val parameterNameDiscoverer = LocalVariableTableParameterNameDiscoverer()

//    if (definition.methods.isEmpty()) {
//        definition.methods = this.selector.method()
//    }

    definition.methods.forEach { requestMethod ->
        definition.path.forEach { path ->
            route(path, HttpMethod.parse(requestMethod.name)) {
                handle {
                    call.request.receiveChannel()
                }
            }
        }
    }
}
