package de.pbauerochse.worklogviewer.youtrack.domain;

import de.pbauerochse.worklogviewer.util.FormattingUtil;

@Deprecated
public class NoSelectionGroupByCategory implements GroupByCategory {

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getName() {
        return FormattingUtil.getFormatted("view.main.groupby.nogroupby");
    }

    @Override
    public boolean isValidYouTrackCategory() {
        return false;
    }
}
