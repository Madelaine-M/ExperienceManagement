package repository.interfaces;

import model.domain.Feedback;
import model.domain.FeedbackItem;

import java.util.List;

public interface FeedbackLookup {

    Feedback findById(int id);

    List<Feedback> findAllByCustomerId(int customerId);

    List<Feedback> findAllByFlightId(int flightId);

    List<FeedbackItem> findItemsByFeedbackId(int id);
}
