package org.jungmha.security.xss

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
