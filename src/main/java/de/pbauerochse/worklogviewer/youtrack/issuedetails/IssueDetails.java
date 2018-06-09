package de.pbauerochse.worklogviewer.youtrack.issuedetails;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by patrick on 31.10.15.
 * {"id":"PATRICK-1","entityId":"87-2","jiraId":null,"field":[],"comment":[],"tag":[]},
 * {"id":"PATRICK-2","entityId":"87-4","jiraId":null,"field":[{"name":"resolved","value":"1446292986266"}],"comment":[],"tag":[]}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueDetails {

    private final String id;
    private final String entityId;
    private final String jiraId;
    private final List<IssueField> fieldList;

    @JsonCreator
    public IssueDetails(@JsonProperty("id") String id,
                        @JsonProperty("entityId") String entityId,
                        @JsonProperty("jiraId") String jiraId,
                        @JsonProperty("field") List<IssueField> fieldList) {
        this.id = id;
        this.entityId = entityId;
        this.jiraId = jiraId;
        this.fieldList = fieldList;
    }

    public String getId() {
        return id;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getJiraId() {
        return jiraId;
    }

    public List<IssueField> getFieldList() {
        return fieldList;
    }

}
