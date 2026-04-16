package repository.interfaces;

import model.Feedback;
import model.FeedbackItem;

import java.util.List;

public interface FeedbackLookup {

    Feedback findById(int id);

    List<Feedback> findAllByCustomerId(int customerId);

    List<Feedback> findAllByFlightId(int flightId);

    List<FeedbackItem> findItemsByFeedbackId(int id);
}
