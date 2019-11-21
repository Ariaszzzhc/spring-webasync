package com.hiarias.webasync

import com.hiarias.webasync.filter.DefaultWebFilterChain
import com.hiarias.webasync.filter.WebFilter
import com.hiarias.webasync.result.method.HandlerMethodArgumentResolver
import com.hiarias.webasync.result.method.annotation.CookieValueMethodArgumentResolver
import com.hiarias.webasync.result.method.annotation.PathVariableMethodArgumentResolver
import com.hiarias.webasync.result.method.annotation.RequestBodyMethodArgumentResolver
import com.hiarias.webasync.result.method.annotation.RequestHeaderMethodArgumentResolver
import com.hiarias.webasync.result.method.annotation.RequestParamMethodArgumentResolver
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.route
import org.springframework.beans.factory.getBeansOfType
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.KotlinReflectionParameterNameDiscoverer
import org.springframework.core.OrderComparator
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer
import kotlin.reflect.full.callSuspend
import kotlin.reflect.jvm.kotlinFunction

class RouteResolver(
    private val context: ConfigurableApplicationContext
) {
    private val factory = context.beanFactory

    private val argumentResolvers: List<HandlerMethodArgumentResolver> = listOf(
        RequestHeaderMethodArgumentResolver(factory),
        RequestParamMethodArgumentResolver(factory, true),
        PathVariableMethodArgumentResolver(factory),
        CookieValueMethodArgumentResolver(factory),
        RequestBodyMethodArgumentResolver()
    )

    fun resolve(route: Route) {
        val bindingContext = BindingContext(ConfigurableWebBindingInitializer())

        val filters = context.getBeansOfType<WebFilter>().values.toList()
        OrderComparator.sort(filters)
        val chain = DefaultWebFilterChain.newInstance(filters)

        generateRouteDefinitions().forEach { definition ->
            val parameterNameDiscoverer = KotlinReflectionParameterNameDiscoverer()
            val method = definition.method
            val bean = definition.bean

            definition.methods.forEach { requestMethod ->
                definition.path.forEach { path ->
                    route.route(path, HttpMethod.parse(requestMethod.name)) {
                        handle {
                            chain.filter(call)

                            val params = handleParameter(
                                method,
                                parameterNameDiscoverer,
                                bindingContext,
                                argumentResolvers
                            )

                            val result = if (method.kotlinFunction != null) {
                                method.kotlinFunction!!.callSuspend(bean, *params.toTypedArray())
                            } else {
                                method.invoke(bean, *params.toTypedArray())
                            }
                            call.respond(result!!)
                        }
                    }
                }
            }
        }
    }

    private fun generateRouteDefinitions(): List<RouteDefinition> {
        val beans = context.getBeansWithAnnotation(Controller::class.java).values

        return beans.flatMap { bean ->
            val ann = bean.javaClass.getDeclaredAnnotation(RequestMapping::class.java)

            val classMapping: List<String> = ann?.value?.toList() ?: emptyList()

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
                    it.path = if (classMapping.isEmpty() && it.path.isEmpty()) {
                        emptyList()
                    } else if (classMapping.isNotEmpty() && it.path.isEmpty()) {
                        classMapping
                    } else if (classMapping.isEmpty() && it.path.isNotEmpty()) {
                        it.path
                    } else {
                        it.path.flatMap { child ->
                            classMapping.map { parent ->
                                parent + if (child.startsWith("/")) child else "/$child"
                            }
                        }
                    }
                }
            }
        }
    }
}
