package de.pbauerochse.worklogviewer.favourites.settings

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Contains elements, that the user marked
 * as favourite so they are presented
 * for easy access in the application
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class FavouritesPersistedSetting @JsonCreator constructor(

    /**
     * Saved searches / search queries
     */
    @JsonProperty("searches")
    val searches : MutableList<PersistedFavouriteSearch> = mutableListOf(),

    /**
     * Saved issues
     */
    @JsonProperty("issues")
    val issues : MutableList<PersistedFavouriteIssue> = mutableListOf()
)