package de.pbauerochse.youtrack.connector.createreport;

/**
 * @author Patrick Bauerochse
 * @since 15.04.15
 */
public abstract class BasicReportDetails {

    private String name;
    private String type;
    private boolean own;
    private ReportParameters parameters = new ReportParameters();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isOwn() {
        return own;
    }

    public void setOwn(boolean own) {
        this.own = own;
    }

    public ReportParameters getParameters() {
        return parameters;
    }

    public void setParameters(ReportParameters parameters) {
        this.parameters = parameters;
    }
}
