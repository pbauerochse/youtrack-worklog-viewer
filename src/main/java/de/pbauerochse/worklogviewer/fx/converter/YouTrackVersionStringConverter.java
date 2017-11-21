package de.pbauerochse.worklogviewer.fx.converter;

import de.pbauerochse.worklogviewer.util.FormattingUtil;
import de.pbauerochse.worklogviewer.youtrack.YouTrackVersion;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;


public class YouTrackVersionStringConverter extends StringConverter<YouTrackVersion> {

    @Override
    public String toString(YouTrackVersion object) {
        return FormattingUtil.getFormatted(object.getLabelKey());
    }

    @Override
    public YouTrackVersion fromString(String string) {
        for (YouTrackVersion method : YouTrackVersion.values()) {
            if (StringUtils.equals(FormattingUtil.getFormatted(method.getLabelKey()), string)) {
                return method;
            }
        }
        return null;
    }

}
