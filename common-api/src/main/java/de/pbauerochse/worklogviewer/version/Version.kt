package de.pbauerochse.worklogviewer.version

/**
 * Defines the three values of a software application version
 */
data class Version(
    val major: Int,
    val minor: Int,
    val release: Int
) : Comparable<Version> {

    fun isNewerThan(other: Version): Boolean {
        return major > other.major ||
                major == other.major && minor > other.minor ||
                major == other.major && minor == other.minor && release > other.release

    }

    override fun compareTo(other: Version): Int {
        val majorComparison = major.compareTo(other.major)
        val minorComparison = minor.compareTo(other.minor)
        val releaseComparison = release.compareTo(other.release)

        return when (majorComparison) {
            0 -> if (minorComparison == 0) releaseComparison else minorComparison
            else -> majorComparison
        }
    }

    override fun toString(): String = "v$major.$minor.$release"

    companion object {
        @JvmStatic
        fun fromVersionString(versionString: String): Version {
            val split = versionString.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return Version(split[0].toInt(), split[1].toInt(), split[2].toInt())
        }
    }

}
