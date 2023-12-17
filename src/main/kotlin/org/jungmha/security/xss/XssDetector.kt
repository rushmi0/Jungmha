package org.jungmha.security.xss

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class XssDetector {

    companion object {

        private val htmlTagsPattern = "<[^>]*>".toRegex()
        private val jsPattern = ".*<script>.*</script>.*".toRegex()

        fun containsXss(text: String): Boolean {
            return containsHtmlTags(text) || containsJavascript(text)
        }

        private fun containsHtmlTags(text: String): Boolean {
            return htmlTagsPattern.containsMatchIn(text)
        }

        private fun containsJavascript(text: String): Boolean {
            return jsPattern.containsMatchIn(text)
        }
    }
}