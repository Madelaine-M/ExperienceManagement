package service.implementation;

import model.Feedback;
import model.FeedbackItem;
import repository.interfaces.FeedbackAnalytics;
import repository.interfaces.FeedbackLookup;
import repository.interfaces.FeedbackUpdate;
import service.interfaces.frontend.FeedbackService;

import java.util.List;

public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackUpdate feedbackUpdate;
    private final FeedbackLookup feedbackLookup;
    private final FeedbackAnalytics feedbackAnalytics;
    public FeedbackServiceImpl(FeedbackAnalytics feedbackAnalytics, FeedbackLookup feedbackLookup, FeedbackUpdate feedbackUpdate) {
        this.feedbackUpdate = feedbackUpdate;
        this.feedbackLookup = feedbackLookup;
        this.feedbackAnalytics = feedbackAnalytics;
    }

    @Override
    public void save(Feedback feedback) {
        feedbackUpdate.save(feedback);
    }

    @Override
    public Feedback findById(int id) {
        return feedbackLookup.findById(id);
    }

    @Override
    public List<Feedback> findAllByCustomerId(int customerId) {
        return feedbackLookup.findAllByCustomerId(customerId);
    }

    @Override
    public List<Feedback> findAllByFlightId(int flightId) {
        return feedbackLookup.findAllByFlightId(flightId);
    }

    @Override
    public List<FeedbackItem> findItemsByFeedbackId(int id) {
        return feedbackLookup.findItemsByFeedbackId(id);
    }

    @Override
    public List<Feedback> findByOverallRatingLessThan(int threshold) {
        return feedbackAnalytics.findByOverallRatingLessThan(threshold);
    }

    @Override
    public List<FeedbackItem> findLowScores(int maxScore) {
        return feedbackAnalytics.findLowScores(maxScore);
    }

    @Override
    public List<FeedbackItem> findByCategory(String category) {
        return feedbackAnalytics.findByCategory(category);
    }

    @Override
    public double getAverageRating(String category) {
        return feedbackAnalytics.getAverageRating(category);
    }
}
