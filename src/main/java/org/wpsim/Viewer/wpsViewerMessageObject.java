package org.wpsim.Viewer;

public class wpsViewerMessageObject {
    private String agentAlias;
    private String message;

    public wpsViewerMessageObject() {
    }

    public wpsViewerMessageObject(String userName, String message) {
        super();
        this.agentAlias = userName;
        this.message = message;
    }

    public String getAgentAlias() {
        return agentAlias;
    }
    public void setAgentAlias(String agentAlias) {
        this.agentAlias = agentAlias;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
