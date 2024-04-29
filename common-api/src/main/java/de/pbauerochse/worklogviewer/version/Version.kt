package de.pbauerochse.worklogviewer.version

/**
 * Defines the three values of a software application version
 */
data class Version @JvmOverloads constructor(
    val major: Int,
    val minor: Int,
    val bugfix: Int,
    val suffix: String? = null
) : Comparable<Version> {

    fun isNewerThan(other: Version): Boolean {
        return major > other.major ||
                major == other.major && minor > other.minor ||
                major == other.major && minor == other.minor && bugfix > other.bugfix
    }

    override fun compareTo(other: Version): Int {
        val majorComparison = major.compareTo(other.major)
        val minorComparison = minor.compareTo(other.minor)
        val releaseComparison = bugfix.compareTo(other.bugfix)

        return when (majorComparison) {
            0 -> if (minorComparison == 0) releaseComparison else minorComparison
            else -> majorComparison
        }
    }

    override fun toString(): String = "v$major.$minor.$bugfix${suffix?.let { "-$it" } ?: ""}"

    companion object {
        @JvmStatic
        fun fromVersionString(versionString: String): Version {
            val split = versionString.removePrefix("v").split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val suffixed = split[2].split("-")
            return Version(
                major = split.getOrElse(0) { "0" }.toInt(),
                minor = split.getOrElse(1) { "0" }.toInt(),
                bugfix = suffixed.getOrElse(0) { "0" }.toInt(),
                suffix = suffixed.getOrNull(1)
            )
        }
    }
}