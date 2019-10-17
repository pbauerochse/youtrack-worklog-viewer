package de.pbauerochse.worklogviewer.fx.issuesearch

/**
 * Removes unwanted HTML from Issue-Description
 * so there are no HTML-Links being displayed
 * in the WebView as they won't be working anyways
 */
object WebViewSanitizer {

    private val LINK_EXPRESSION = Regex("<a((\\s*\\w+=\".*?\"\\s*)*)>(.+?)</a>")
    private val ATTRIBUTE_EXPRESSION = Regex("\\w+=\".*?\"")

    fun sanitize(description: String): String {
        return replaceLinks(description)
    }

    private fun replaceLinks(description: String): String {
        return LINK_EXPRESSION.replace(description) { matchResult ->
            val attributesAndValues = ATTRIBUTE_EXPRESSION.findAll(matchResult.groupValues[1]).map { it.groupValues[0].trim() }.toList()
            val attributes = readAttributeMap(attributesAndValues)
            val linkText = matchResult.groupValues[3]

            val additionalClasses = attributes["class"]?.let { " $it" } ?: ""

            val titleAttribute = attributes["title"]?.let { " title=\"$it\"" } ?: ""
            val classValue = "link$additionalClasses"

            return@replace "<span class=\"$classValue\"$titleAttribute>$linkText</span>"
        }
    }

    private fun readAttributeMap(attributes: List<String>): Map<String, String> {
        return attributes.filter { it.isNotBlank() }.associate { attribute ->
            val key = attribute.substringBefore("=")
            val value = attribute.substringAfter("=").trim().removeSurrounding("\"")
            return@associate Pair(key, value)
        }
    }


}