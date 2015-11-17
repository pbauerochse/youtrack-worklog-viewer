package de.pbauerochse.worklogviewer.youtrack.issuedetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.Lists;

import java.util.List;

/**
 * Created by patrick on 31.10.15.
 * {"issue":[{"id":"PATRICK-1","entityId":"87-2","jiraId":null,"field":[],"comment":[],"tag":[]},{"id":"PATRICK-2","entityId":"87-4","jiraId":null,"field":[{"name":"resolved","value":"1446292986266"}],"comment":[],"tag":[]}]}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueDetailsResponse {

    @JsonProperty("issue")
    private List<IssueDetails> issues = Lists.newArrayList();

    public List<IssueDetails> getIssues() {
        return issues;
    }

    public void setIssues(List<IssueDetails> issues) {
        this.issues = issues;
    }
}
