package repository.interfaces;

import model.domain.Feedback;
import model.domain.FeedbackItem;

public interface FeedbackUpdate {

    void save(Feedback feedback);

}
