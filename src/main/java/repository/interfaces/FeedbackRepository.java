package repository.interfaces;

import model.Feedback;
import model.FeedbackItem;

import java.util.List;

public interface FeedbackRepository {

    void save(Feedback feedback);

    Feedback findById(int id);

    List<Feedback> findAllByCustomerId(int customerId);

    List<FeedbackItem> findLowScores(int maxScore);

    List<FeedbackItem> findByCategory(String category);

    List<Feedback> findByOverallRatingLessThan(int threshold);

    void deleteById(int id);
}
