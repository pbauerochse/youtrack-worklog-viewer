package de.pbauerochse.worklogviewer.youtrack.v20174.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomField {

    private final String localizedName;
    private final String name;
    private final String id;
    private final FieldType fieldType;

    @JsonCreator
    public CustomField(@JsonProperty("localizedName") String localizedName,
                       @JsonProperty("name") String name,
                       @JsonProperty("id") String id,
                       @JsonProperty("fieldType") FieldType fieldType) {
        this.localizedName = localizedName;
        this.name = name;
        this.id = id;
        this.fieldType = fieldType;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    @JsonProperty("$type")
    public String getType() {
        return "jetbrains.charisma.persistence.customfields.CustomField";
    }

}
