package ui.model;

public class JourneyStepView {
    private final String label;
    private final JourneyStepState state;

    public JourneyStepView(String label, JourneyStepState state) {
        this.label = label;
        this.state = state;
    }

    public String getLabel() {
        return label;
    }

    public JourneyStepState getState() {
        return state;
    }
}
