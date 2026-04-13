package repository.interfaces;

import model.Feedback;
import model.FeedbackItem;

import java.util.List;

public interface FeedbackAnalytics {

    List<Feedback> findByOverallRatingLessThan(int threshold);

    List<FeedbackItem> findLowScores(int maxScore);

    List<FeedbackItem> findByCategory(String category);

    double getAverageRating(String category);
}
