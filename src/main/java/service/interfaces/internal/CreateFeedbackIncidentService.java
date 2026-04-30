package service.interfaces.internal;

import model.domain.FeedbackIncident;
import model.domain.FeedbackItem;

import java.util.List;

public interface CreateFeedbackIncidentService {
    List<FeedbackIncident> createFeedbackIncident(List<FeedbackItem> feedbackIncident);
}
