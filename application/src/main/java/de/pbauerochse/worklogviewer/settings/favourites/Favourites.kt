package de.pbauerochse.worklogviewer.settings.favourites

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Contains elements, that the user marked
 * as favourite so they are presented
 * for easy access in the applicaton
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Favourites {

    /**
     * Saved searches / search queries
     */
    val searches : MutableList<FavouriteSearch> = mutableListOf()

    /**
     * Saved issues
     */
    val issues : MutableList<FavouriteIssue> = mutableListOf()

}