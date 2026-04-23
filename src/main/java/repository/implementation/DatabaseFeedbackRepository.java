package repository.implementation;

import model.Feedback;
import model.FeedbackItem;
import repository.interfaces.FeedbackAnalytics;
import repository.interfaces.FeedbackLookup;
import repository.interfaces.FeedbackUpdate;
import repository.interfaces.NPSScores;

import java.util.List;

public class DatabaseFeedbackRepository implements FeedbackLookup, FeedbackUpdate, FeedbackAnalytics, NPSScores {
    private final FeedbackLookup feedbackLookup;
    private final FeedbackUpdate feedbackUpdate;
    private final FeedbackAnalytics feedbackAnalytics;
    private final NPSScores npsScores;

    public DatabaseFeedbackRepository() {
        this(
                new DatabaseFeedbackLookup(),
                new DatabaseFeedbackUpdate(),
                new DatabaseFeedbackAnalytics(),
                new DatabaseNPSScores()
        );
    }

    public DatabaseFeedbackRepository(FeedbackLookup feedbackLookup, FeedbackUpdate feedbackUpdate,
                                      FeedbackAnalytics feedbackAnalytics, NPSScores npsScores) {
        this.feedbackLookup = feedbackLookup;
        this.feedbackUpdate = feedbackUpdate;
        this.feedbackAnalytics = feedbackAnalytics;
        this.npsScores = npsScores;
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
    public void save(Feedback feedback) {
        feedbackUpdate.save(feedback);
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

    @Override
    public int countByScoreRange(int min, int max) {
        return npsScores.countByScoreRange(min, max);
    }
}
