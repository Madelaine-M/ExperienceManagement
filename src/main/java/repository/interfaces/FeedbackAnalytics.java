package repository.interfaces;

import model.domain.Feedback;
import model.domain.FeedbackItem;

import java.util.List;

public interface FeedbackAnalytics {

    List<FeedbackItem> findLowScores(int maxScore);

    List<FeedbackItem> findByCategory(String category);

    double getAverageRating(String category);
}
