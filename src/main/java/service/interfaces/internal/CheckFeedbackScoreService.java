package service.interfaces.internal;

import model.domain.FeedbackIncident;
import model.domain.FeedbackItem;

import java.util.List;

public interface CheckFeedbackScoreService {

    List<FeedbackItem> findLowScores(int maxScore);
}
