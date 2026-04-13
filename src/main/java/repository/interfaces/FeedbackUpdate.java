package repository.interfaces;

import model.Feedback;
import model.FeedbackItem;

public interface FeedbackUpdate {

    void save(Feedback feedback);

    void save(FeedbackItem item);
}
