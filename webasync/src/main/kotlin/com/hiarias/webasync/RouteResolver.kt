package com.hiarias.webasync

import com.hiarias.webasync.result.method.HandlerMethodArgumentResolver
import com.hiarias.webasync.result.method.annotation.*
import io.ktor.http.HttpMethod
import io.ktor.routing.Route
import io.ktor.routing.route
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.LocalVariableTableParameterNameDiscoverer
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer

class RouteResolver(
    private val context: ConfigurableApplicationContext
) {
    private val factory = context.beanFactory

    private val argumentResolvers: List<HandlerMethodArgumentResolver> = listOf(
        RequestHeaderMethodArgumentResolver(factory),
        RequestParamMethodArgumentResolver(factory, true),
        PathVariableMethodArgumentResolver(factory),
        CookieValueMethodArgumentResolver(factory)
    )

    fun resolve(route: Route) {
        val bindingContext = BindingContext(ConfigurableWebBindingInitializer())

        generateRouteDefinitions().forEach { definition ->
            val parameterNameDiscoverer = LocalVariableTableParameterNameDiscoverer()
            val method = definition.method

            definition.methods.forEach { requestMethod ->
                definition.path.forEach { path ->
                    route.route(path, HttpMethod.parse(requestMethod.name)) {
                        handle {
                            handleParameter(
                                method,
                                parameterNameDiscoverer,
                                bindingContext,
                                argumentResolvers
                            )
                        }
                    }
                }
            }
        }
    }

    private fun generateRouteDefinitions(): List<RouteDefinition> {
        val beans = context.getBeansWithAnnotation(RestController::class.java)

        return beans.flatMap { bean ->
            val classMapping = bean.javaClass.getDeclaredAnnotation(RequestMapping::class.java)

            bean.javaClass.methods.mapNotNull { method ->
                method.getDeclaredAnnotation(RequestMapping::class.java)?.let {
                    RouteDefinition(method, bean, it.method.toList(), it.value.toList())
                } ?: method.getDeclaredAnnotation(GetMapping::class.java)?.let {
                    RouteDefinition(method, bean, listOf(RequestMethod.GET), it.value.toList())
                } ?: method.getDeclaredAnnotation(PostMapping::class.java)?.let {
                    RouteDefinition(method, bean, listOf(RequestMethod.POST), it.value.toList())
                } ?: method.getDeclaredAnnotation(DeleteMapping::class.java)?.let {
                    RouteDefinition(method, bean, listOf(RequestMethod.DELETE), it.value.toList())
                } ?: method.getDeclaredAnnotation(PutMapping::class.java)?.let {
                    RouteDefinition(method, bean, listOf(RequestMethod.PUT), it.value.toList())
                }
            }.apply {
                forEach {
                    it.path = it.path.flatMap { child ->
                        classMapping.value.map { parent ->
                            parent + if (child.startsWith("/")) child else "/$child"
                        }
                    }
                }
            }
        }
    }
}
