package de.pbauerochse.worklogviewer.settings.favourites

import com.fasterxml.jackson.annotation.*
import de.pbauerochse.worklogviewer.report.MinimalIssue

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class FavouriteIssue @JsonCreator constructor(
    @JsonProperty("id") override val id: String,
    @JsonProperty("summary") override val summary: String
) : Favourite, MinimalIssue {

    constructor(issue: MinimalIssue) : this(issue.id, issue.summary)

    @JsonIgnore
    val fullTitle: String = "$id - $summary"

}
