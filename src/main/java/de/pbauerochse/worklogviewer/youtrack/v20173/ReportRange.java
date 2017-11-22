package de.pbauerochse.worklogviewer.youtrack.v20173;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FixedReportRange.class, name = "fixed"),
        @JsonSubTypes.Type(value = NamedReportRange.class, name = "named")
})
public interface ReportRange {

}
