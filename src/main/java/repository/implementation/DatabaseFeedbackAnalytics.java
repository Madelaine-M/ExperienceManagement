package repository.implementation;

import database.connection.ConnectionProvider;
import model.domain.FeedbackItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.RepositoryException;
import repository.implementation.mapper.FeedbackItemResultSetMapper;
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
    private final FeedbackItemResultSetMapper feedbackItemMapper;

    public DatabaseFeedbackAnalytics(ConnectionProvider connectionProvider,
                                     FeedbackItemResultSetMapper feedbackItemMapper) {
        this.connectionProvider = connectionProvider;
        this.feedbackItemMapper = feedbackItemMapper;
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
            throw new RepositoryException("Failed to load feedback items with score <= " + maxScore, e);
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
            throw new RepositoryException("Failed to load feedback items by category " + category, e);
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
            throw new RepositoryException("Failed to calculate average rating for category " + category, e);
        }

        return 0.0;
    }
}
