package de.pbauerochse.worklogviewer.youtrack.issuedetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.Lists;

import java.util.List;

/**
 * Created by patrick on 31.10.15.
 * {"id":"PATRICK-1","entityId":"87-2","jiraId":null,"field":[],"comment":[],"tag":[]},
 * {"id":"PATRICK-2","entityId":"87-4","jiraId":null,"field":[{"name":"resolved","value":"1446292986266"}],"comment":[],"tag":[]}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueDetails {

    private String id;
    private String entityId;
    private String jiraId;

    @JsonProperty("field")
    private List<IssueField> fieldList = Lists.newArrayList();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getJiraId() {
        return jiraId;
    }

    public void setJiraId(String jiraId) {
        this.jiraId = jiraId;
    }

    public List<IssueField> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<IssueField> fieldList) {
        this.fieldList = fieldList;
    }
}
