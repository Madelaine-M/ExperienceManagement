package simulation.engine;

record FeedbackGenerationResult(int generatedFeedbacks, int generatedFeedbackIncidents) {
    static FeedbackGenerationResult none() {
        return new FeedbackGenerationResult(0, 0);
    }
}
