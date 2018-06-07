package de.pbauerochse.worklogviewer.youtrack.createreport;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FixedReportRange.class, name = "fixed"),
        @JsonSubTypes.Type(value = NamedReportRange.class, name = "named")
})
public interface ReportRange {

}
