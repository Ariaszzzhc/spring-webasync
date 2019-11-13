package com.hiarias.webasync

import org.springframework.web.bind.annotation.RequestMethod
import java.lang.reflect.Method

data class RouteDefinition(
    val method: Method,
    val bean: Any,
    var methods: List<RequestMethod>,
    var path: List<String>
)
