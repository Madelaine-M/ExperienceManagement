package model.workflow;

public class RecommendationEmailDraft {
    private final int actionId;
    private final int optionNumber;
    private final int incidentId;
    private final int customerId;
    private final int advisorId;
    private final String customerName;
    private final String advisorName;
    private final String selectedSuggestion;
    private final String subject;
    private final String body;

    public RecommendationEmailDraft(int actionId,
                                    int optionNumber,
                                    int incidentId,
                                    int customerId,
                                    int advisorId,
                                    String customerName,
                                    String advisorName,
                                    String selectedSuggestion,
                                    String subject,
                                    String body) {
        this.actionId = actionId;
        this.optionNumber = optionNumber;
        this.incidentId = incidentId;
        this.customerId = customerId;
        this.advisorId = advisorId;
        this.customerName = customerName;
        this.advisorName = advisorName;
        this.selectedSuggestion = selectedSuggestion;
        this.subject = subject;
        this.body = body;
    }

    public int getActionId() {
        return actionId;
    }

    public int getOptionNumber() {
        return optionNumber;
    }

    public int getIncidentId() {
        return incidentId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getAdvisorId() {
        return advisorId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getAdvisorName() {
        return advisorName;
    }

    public String getSelectedSuggestion() {
        return selectedSuggestion;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
