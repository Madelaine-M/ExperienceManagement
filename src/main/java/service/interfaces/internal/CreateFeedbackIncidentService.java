package service.interfaces.internal;

import model.FeedbackIncident;
import model.FeedbackItem;

import java.util.List;

public interface CreateFeedbackIncidentService {
    void createFeedbackIncident(List<FeedbackItem> feedbackIncident);
}
