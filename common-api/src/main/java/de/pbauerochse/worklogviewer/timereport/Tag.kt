package de.pbauerochse.worklogviewer.timereport

class Tag(
    val label: String,
    backgroundColor: String? = null,
    foregroundColor: String? = null
) {
    val backgroundColorHex = backgroundColor?.let { it.takeIf { it.startsWith("#") } ?: "#$it" }
    val foregroundColorHex = foregroundColor?.let { it.takeIf { it.startsWith("#") } ?: "#$it" }

    override fun toString(): String {
        return "Tag(label='$label', backgroundColorHex='$backgroundColorHex', foregroundColorHex='$foregroundColorHex')"
    }
}
