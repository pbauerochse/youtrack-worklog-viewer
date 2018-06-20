package de.pbauerochse.worklogviewer.logging;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public class LimitedLogMessageBuilderTest {

    @Test
    public void test() {
        LimitedLogMessageBuilder messageBuilder = new LimitedLogMessageBuilder(10);
        for (int i = 0; i < 20; i++) {
            messageBuilder.onLogMessage(String.valueOf(i), null);
        }

        Assert.assertEquals(messageBuilder.getAllMessages(), "10111213141516171819");
    }

}
