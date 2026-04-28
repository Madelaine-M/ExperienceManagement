package service.implementation;

import model.FeedbackItem;
import repository.interfaces.FeedbackAnalytics;
import service.interfaces.internal.CheckFeedbackScoreService;

import java.util.List;

public class CheckFeedbackScoreServiceImpl implements CheckFeedbackScoreService {
    private final FeedbackAnalytics feedbackAnalytics;

    public CheckFeedbackScoreServiceImpl(FeedbackAnalytics feedbackAnalytics) {
        this.feedbackAnalytics = feedbackAnalytics;
    }

    @Override
    public List<FeedbackItem> findLowScores(int maxScore) {
        return feedbackAnalytics.findLowScores(maxScore);
    }
}
