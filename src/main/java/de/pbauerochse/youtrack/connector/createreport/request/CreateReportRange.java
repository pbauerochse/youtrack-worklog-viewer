package de.pbauerochse.youtrack.connector.createreport.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Patrick Bauerochse
 * @since 14.04.15
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FixedReportRange.class, name = "fixed"),
        @JsonSubTypes.Type(value = NamedReportRange.class, name = "named")
})
public interface CreateReportRange {

}
