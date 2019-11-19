package com.hiarias.webasync

import io.ktor.application.ApplicationCall
import io.ktor.http.content.PartData
import io.ktor.request.isMultipart
import io.ktor.request.receiveMultipart
import org.springframework.beans.MutablePropertyValues
import org.springframework.util.CollectionUtils
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.validation.DataBinder
import org.springframework.web.bind.WebDataBinder
import java.util.TreeMap

class ApplicationCallDataBinder(
    target: Any?,
    objectName: String = DataBinder.DEFAULT_OBJECT_NAME
) : WebDataBinder(target, objectName) {
    suspend fun bind(applicationCall: ApplicationCall) {
        getValuesToBind(applicationCall).also {
            doBind(MutablePropertyValues(it))
        }
    }

    protected suspend fun getValuesToBind(applicationCall: ApplicationCall): Map<String, Any> {
        return extractValuesToBind(applicationCall)
    }

    companion object {
        suspend fun extractValuesToBind(applicationCall: ApplicationCall): Map<String, Any> {
            val formData: MultiValueMap<String, String> = LinkedMultiValueMap()

            if (!applicationCall.request.isMultipart()) {
                val multipart = applicationCall.receiveMultipart()
                while (true) {
                    val part = multipart.readPart() ?: break
                    when (part) {
                        is PartData.FormItem -> formData[part.name!!] = part.value
                        is PartData.FileItem -> TODO("MultiPartFile not implement yet")
                    }
                    part.dispose()
                }
            }

            return TreeMap<String, Any>().apply {
                applicationCall.request.queryParameters.forEach { key, values ->
                    addBindValue(this, key, values)
                }

                formData.forEach { (key, values) ->
                    addBindValue(this, key, values)
                }

                //TODO MultiPartFile
            }
        }

        private fun addBindValue(params: MutableMap<String, Any>, key: String, values: List<Any>) {
            if (!CollectionUtils.isEmpty(values)) {
                params[key] = if (values.size == 1) values[0] else values
            }
        }
    }
}
