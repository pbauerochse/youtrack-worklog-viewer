package de.pbauerochse.worklogviewer.youtrack.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.pbauerochse.worklogviewer.util.FormattingUtil;

/**
 * A category which can be used to group
 * tasks within the time report
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupByCategory {

    public static final GroupByCategory NO_SELECTION_ITEM = new GroupByCategory(null, FormattingUtil.getFormatted("view.main.groupby.nogroupby"));

    private final String id;
    private final String name;

    @JsonCreator
    public GroupByCategory(@JsonProperty("id") String id, @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public boolean isNoSelection() {
        return NO_SELECTION_ITEM == this;
    }

}
