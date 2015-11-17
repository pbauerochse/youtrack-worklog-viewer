package de.pbauerochse.worklogviewer.fx.converter;

import de.pbauerochse.worklogviewer.util.FormattingUtil;
import de.pbauerochse.worklogviewer.youtrack.connector.YouTrackAuthenticationMethod;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Patrick Bauerochse
 * @since 14.10.15
 */
public class YouTrackAuthenticationMethodStringConverter extends StringConverter<YouTrackAuthenticationMethod> {

    @Override
    public String toString(YouTrackAuthenticationMethod object) {
        return FormattingUtil.getFormatted(object.getLabelKey());
    }

    @Override
    public YouTrackAuthenticationMethod fromString(String string) {
        for (YouTrackAuthenticationMethod method : YouTrackAuthenticationMethod.values()) {
            if (StringUtils.equals(FormattingUtil.getFormatted(method.getLabelKey()), string)) {
                return method;
            }
        }
        return null;
    }

}
