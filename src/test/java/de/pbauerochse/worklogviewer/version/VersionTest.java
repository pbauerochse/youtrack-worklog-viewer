package de.pbauerochse.worklogviewer.version;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by patrick on 01.11.15.
 */
public class VersionTest {

    @Test
    public void testVersions() {
        Version versionA = new Version("1.1.1");
        Version versionB = new Version("1.1.1");

        Assert.assertFalse(versionA.isNewerThan(versionB));
        Assert.assertFalse(versionB.isNewerThan(versionA));

        versionB = new Version("1.1.2");
        Assert.assertFalse(versionA.isNewerThan(versionB));
        Assert.assertTrue(versionB.isNewerThan(versionA));

        versionB = new Version("0.1.2");
        Assert.assertTrue(versionA.isNewerThan(versionB));
        Assert.assertFalse(versionB.isNewerThan(versionA));

        versionB = new Version("1.2.0");
        Assert.assertFalse(versionA.isNewerThan(versionB));
        Assert.assertTrue(versionB.isNewerThan(versionA));

        versionB = new Version("2.0.0");
        Assert.assertFalse(versionA.isNewerThan(versionB));
        Assert.assertTrue(versionB.isNewerThan(versionA));
    }

}
