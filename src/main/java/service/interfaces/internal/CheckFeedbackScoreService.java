package service.interfaces.internal;

import model.FeedbackIncident;
import model.FeedbackItem;

import java.util.List;

public interface CheckFeedbackScoreService {

    List<FeedbackItem> findLowScores(int maxScore);
}
