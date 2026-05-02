package service.interfaces.frontend;

import model.domain.Feedback;
import model.domain.FeedbackItem;

import java.util.List;

public interface FeedbackService {
    void save(Feedback feedback);

    Feedback findById(int id);

    List<Feedback> findAllByCustomerId(int customerId);

    List<Feedback> findAllByFlightId(int flightId);

    List<FeedbackItem> findItemsByFeedbackId(int id);

    List<FeedbackItem> findLowScores(int maxScore);

    List<FeedbackItem> findByCategory(String category);

    double getAverageRating(String category);
}
