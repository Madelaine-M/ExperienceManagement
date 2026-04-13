package repository.interfaces;

import model.Feedback;
import model.FeedbackItem;

import java.util.List;

public interface FeedbackLookup {

    Feedback findById(int id);

    List<Feedback> findAllByCustomerId(int customerId);

    List<FeedbackItem> findItemsByFeedbackId(int id);
}
