package repository.implementation;

import database.connection.ConnectionProvider;
import database.connection.DatabaseConnectionProvider;
import model.Feedback;
import model.FeedbackItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.implementation.mapper.FeedbackItemResultSetMapper;
import repository.implementation.mapper.FeedbackResultSetMapper;
import repository.implementation.support.FeedbackItemLoader;
import repository.interfaces.FeedbackAnalytics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFeedbackAnalytics implements FeedbackAnalytics {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseFeedbackAnalytics.class);
    private final ConnectionProvider connectionProvider;
    private final FeedbackResultSetMapper feedbackMapper;
    private final FeedbackItemResultSetMapper feedbackItemMapper;
    private final FeedbackItemLoader feedbackItemLoader;

    public DatabaseFeedbackAnalytics() {
        this(
                new DatabaseConnectionProvider(),
                new FeedbackResultSetMapper(),
                new FeedbackItemResultSetMapper(),
                new FeedbackItemLoader()
        );
    }

    public DatabaseFeedbackAnalytics(ConnectionProvider connectionProvider, FeedbackResultSetMapper feedbackMapper,
                                     FeedbackItemResultSetMapper feedbackItemMapper,
                                     FeedbackItemLoader feedbackItemLoader) {
        this.connectionProvider = connectionProvider;
        this.feedbackMapper = feedbackMapper;
        this.feedbackItemMapper = feedbackItemMapper;
        this.feedbackItemLoader = feedbackItemLoader;
    }

    @Override
    public List<Feedback> findByOverallRatingLessThan(int threshold) {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = "SELECT * FROM feedbacks WHERE total_score < ? ORDER BY total_score ASC, id ASC;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, threshold);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Feedback feedback = feedbackMapper.map(rs);
                    feedback.setItems(feedbackItemLoader.loadItemsByFeedbackId(conn, feedback.getId()));
                    feedbacks.add(feedback);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading feedbacks below rating {}", threshold, e);
        }

        return feedbacks;
    }

    @Override
    public List<FeedbackItem> findLowScores(int maxScore) {
        List<FeedbackItem> items = new ArrayList<>();
        String sql = "SELECT * FROM feedback_items WHERE score <= ? ORDER BY score ASC, id ASC;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maxScore);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(feedbackItemMapper.map(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading feedback items with score <= {}", maxScore, e);
        }

        return items;
    }

    @Override
    public List<FeedbackItem> findByCategory(String category) {
        List<FeedbackItem> items = new ArrayList<>();
        String sql = "SELECT * FROM feedback_items WHERE category = ? ORDER BY id ASC;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(feedbackItemMapper.map(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading feedback items by category {}", category, e);
        }

        return items;
    }

    @Override
    public double getAverageRating(String category) {
        String sql = "SELECT AVG(score) AS average_score FROM feedback_items WHERE category = ?;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("average_score");
                }
            }
        } catch (SQLException e) {
            logger.error("Error while calculating average rating for category {}", category, e);
        }

        return 0.0;
    }
}
