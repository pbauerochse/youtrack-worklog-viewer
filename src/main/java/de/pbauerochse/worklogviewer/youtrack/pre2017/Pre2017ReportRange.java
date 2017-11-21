package de.pbauerochse.worklogviewer.youtrack.pre2017;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Pre2017FixedReportRange.class, name = "fixed"),
        @JsonSubTypes.Type(value = Pre2017NamedReportRange.class, name = "named")
})
public interface Pre2017ReportRange {

}
