package de.pbauerochse.youtrack.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Patrick Bauerochse
 * @since 14.04.15
 */
public class PasswordUtilTest {

    @Test
    public void performTest() throws Exception {
        String cleartextPassword = "super g€h€ime$ passwört";
        String encryptPassword = PasswordUtil.encryptCleartextPassword(cleartextPassword);

        Assert.assertNotEquals("Klartext Passwort und verschlüsseltes Passwort sind identisch", cleartextPassword, encryptPassword);

        String entschluesseltesPasswort = PasswordUtil.decryptEncryptedPassword(encryptPassword);

        Assert.assertEquals(cleartextPassword, entschluesseltesPasswort);
    }

}
