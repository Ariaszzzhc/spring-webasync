package com.hiarias.webasync.result.method.annotation

import com.hiarias.webasync.BindingContext
import io.ktor.http.HttpMethod
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.SynthesizingMethodParameter
import org.springframework.format.support.DefaultFormattingConversionService
import org.springframework.util.ReflectionUtils
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer
import org.springframework.web.server.ServerWebInputException

@ExperimentalCoroutinesApi
class RequestParamMethodArgumentResolverTests {
    lateinit var resolver: RequestParamMethodArgumentResolver
    lateinit var bindingContext: BindingContext

    lateinit var nullableParamRequired: MethodParameter
    lateinit var nullableParamNotRequired: MethodParameter
    lateinit var nonNullableParamRequired: MethodParameter
    lateinit var nonNullableParamNotRequired: MethodParameter

    @BeforeEach
    fun setup() {
        this.resolver = RequestParamMethodArgumentResolver(null, true)
        val initializer = ConfigurableWebBindingInitializer()
        initializer.conversionService = DefaultFormattingConversionService()
        bindingContext = BindingContext(initializer)

        val method = ReflectionUtils.findMethod(javaClass, "handle", String::class.java,
            String::class.java, String::class.java, String::class.java)!!

        nullableParamRequired = SynthesizingMethodParameter(method, 0)
        nullableParamNotRequired = SynthesizingMethodParameter(method, 1)
        nonNullableParamRequired = SynthesizingMethodParameter(method, 2)
        nonNullableParamNotRequired = SynthesizingMethodParameter(method, 3)
    }

    @Test
    fun resolveNullableRequiredWithParameter() {
        withTestApplication {
            val call = handleRequest(HttpMethod.Get, "/path?name=123")

            runBlockingTest {
                val result = resolver.resolveArgument(nullableParamRequired, bindingContext, call)
                Assertions.assertEquals("123", result) {
                    "parameter 'name' should be '123'"
                }
            }
        }
    }

    @Test
    fun resolveNullableRequiredWithoutParameter() {
        withTestApplication {
            val call = handleRequest(HttpMethod.Get, "/")

            runBlockingTest {
                val result = resolver.resolveArgument(nullableParamRequired, bindingContext, call)
                Assertions.assertNull(result) {
                    "parameter 'name' should be null"
                }
            }
        }
    }

    @Test
    fun resolveNullableNotRequiredWithParameter() {
        withTestApplication {
            val call = handleRequest(HttpMethod.Get, "/path?name=123")

            runBlockingTest {
                val result = resolver.resolveArgument(nullableParamNotRequired, bindingContext, call)
                Assertions.assertEquals("123", result) {
                    "parameter 'name' should be '123'"
                }
            }
        }
    }

    @Test
    fun resolveNullableNotRequiredWithoutParameter() {
        withTestApplication {
            val call = handleRequest(HttpMethod.Get, "/")

            runBlockingTest {
                val result = resolver.resolveArgument(nullableParamNotRequired, bindingContext, call)
                Assertions.assertNull(result) {
                    "parameter 'name' should be null"
                }
            }
        }
    }

    @Test
    fun resolveNonNullableRequiredWithParameter() {
        withTestApplication {
            val call = handleRequest(HttpMethod.Get, "/path?name=123")

            runBlockingTest {
                val result = resolver.resolveArgument(nonNullableParamRequired, bindingContext, call)
                Assertions.assertEquals("123", result) {
                    "parameter 'name' should be '123'"
                }
            }
        }
    }

    @Test
    fun resolveNonNullableRequiredWithoutParameter() {
        assertThrows<ServerWebInputException> {
            withTestApplication {
                val call = handleRequest(HttpMethod.Get, "/")

                runBlockingTest {
                    resolver.resolveArgument(nonNullableParamRequired, bindingContext, call)
                }
            }
        }
    }

    @Test
    fun resolveNonNullableNotRequiredWithParameter() {
        withTestApplication {
            val call = handleRequest(HttpMethod.Get, "/path?name=123")

            runBlockingTest {
                val result = resolver.resolveArgument(nonNullableParamNotRequired, bindingContext, call)
                Assertions.assertEquals("123", result) {
                    "parameter 'name' should be '123'"
                }
            }
        }
    }

    @Suppress("unused_parameter")
    fun handle(
        @RequestParam("name") nullableParamRequired: String?,
        @RequestParam("name", required = false) nullableParamNotRequired: String?,
        @RequestParam("name") nonNullableParamRequired: String,
        @RequestParam("name", required = false) nonNullableParamNotRequired: String) {
    }
}
