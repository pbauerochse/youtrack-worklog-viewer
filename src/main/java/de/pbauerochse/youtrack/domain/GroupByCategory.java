package de.pbauerochse.youtrack.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Patrick Bauerochse
 * @since 08.07.15
 */
public class GroupByCategory {

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public boolean isNoGroupByCriteria() {
        return id == null;
    }

}
