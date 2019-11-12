package com.hiarias.webasync

import org.springframework.context.ApplicationContext
import org.springframework.web.bind.annotation.*

class RouteResolver(
    private val context: ApplicationContext
) {

    fun resolve(): List<RouteDefinition> {
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