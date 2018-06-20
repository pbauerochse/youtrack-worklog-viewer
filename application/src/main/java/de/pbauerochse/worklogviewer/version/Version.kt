package de.pbauerochse.worklogviewer.version

/**
 * Defines the three values of a software application version
 */
class Version(versionString: String) {

    private val major: Int
    private val minor: Int
    private val release: Int

    init {
        val split = versionString.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        major = split[0].toInt()
        minor = split[1].toInt()
        release = split[2].toInt()
    }

    fun isNewerThan(other: Version): Boolean {
        return major > other.major ||
                major == other.major && minor > other.minor ||
                major == other.major && minor == other.minor && release > other.release

    }

    override fun toString(): String = "v$major.$minor.$release"

}
