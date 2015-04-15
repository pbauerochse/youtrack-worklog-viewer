package de.pbauerochse.youtrack.connector.createreport.request;

/**
 * @author Patrick Bauerochse
 * @since 14.04.15
 */
public class CreateReportRange {

    private String type;
    private String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
