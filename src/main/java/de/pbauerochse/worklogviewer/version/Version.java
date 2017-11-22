package de.pbauerochse.worklogviewer.version;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by patrick on 01.11.15.
 */
public class Version {

    private int major;
    private int minor;
    private int release;

    public Version(String versionString) {
        if (StringUtils.isNotBlank(versionString)) {
            String[] split = versionString.split("\\.");

            if (split.length > 0) {
                try {
                    major = Integer.parseInt(StringUtils.trim(split[0]));
                } catch (NumberFormatException e) {
                    // ignore
                }
            }

            if (split.length > 1) {
                try {
                    minor = Integer.parseInt(StringUtils.trim(split[1]));
                } catch (NumberFormatException e) {
                    // ignore
                }
            }

            if (split.length > 2) {
                try {
                    release = Integer.parseInt(StringUtils.trim(split[2]));
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
    }

    public boolean isNewerThan(Version other) {
        return major > other.major ||
                major == other.major && minor > other.minor ||
                major == other.major && minor == other.minor && release > other.release;

    }

    @Override
    public String toString() {
        return Joiner.on('.').join(major, minor, release);
    }
}
