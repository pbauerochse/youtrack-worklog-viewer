package de.pbauerochse.worklogviewer.fx.converter;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Patrick Bauerochse
 * @since 08.07.15
 */
public class ReportTimerangeStringConverter extends StringConverter<ReportTimerange> {

    @Override
    public String toString(ReportTimerange object) {
        return FormattingUtil.getFormatted(object.getLabelKey());
    }

    @Override
    public ReportTimerange fromString(String string) {
        for (ReportTimerange timerange : ReportTimerange.values()) {
            if (StringUtils.equals(FormattingUtil.getFormatted(timerange.getLabelKey()), string)) {
                return timerange;
            }
        }
        return null;
    }

}
