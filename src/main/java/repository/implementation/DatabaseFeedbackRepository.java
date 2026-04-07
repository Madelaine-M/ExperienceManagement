package repository.implementation;

import database.connection.DatabaseManager;
import model.Feedback;
import model.FeedbackItem;
import model.enums.FeedbackCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.interfaces.FeedbackRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFeedbackRepository implements FeedbackRepository {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseFeedbackRepository.class);

    @Override
    public void save(Feedback feedback) {
        String feedbackSql = """
            INSERT INTO feedbacks (customer_id, created_at)
            VALUES (?, ?);
            """;
        String itemSql = """
            INSERT INTO feedback_items (feedback_id, category, score, comment)
            VALUES (?, ?, ?, ?);
            """;

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement feedbackStmt = conn.prepareStatement(feedbackSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement itemStmt = conn.prepareStatement(itemSql, Statement.RETURN_GENERATED_KEYS)) {

                feedbackStmt.setInt(1, feedback.getCustomerId());
                setCreatedAt(feedbackStmt, 2, feedback.getCreatedAt());
                feedbackStmt.executeUpdate();

                int feedbackId = extractGeneratedId(feedbackStmt, "feedback");
                feedback.setId(feedbackId);

                for (FeedbackItem item : feedback.getItems()) {
                    itemStmt.setInt(1, feedbackId);
                    itemStmt.setString(2, item.getCategory() != null ? item.getCategory().name() : null);
                    itemStmt.setInt(3, item.getScore());
                    itemStmt.setString(4, item.getComment());
                    itemStmt.executeUpdate();

                    int itemId = extractGeneratedId(itemStmt, "feedback item");
                    item.setId(itemId);
                    item.setFeedbackId(feedbackId);
                }

                conn.commit();
                logger.info("Feedback {} for customer {} saved with {} items.", feedbackId, feedback.getCustomerId(), feedback.getItems().size());
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.error("Error while saving feedback", e);
        }
    }

    @Override
    public Feedback findById(int id) {
        String feedbackSql = "SELECT * FROM feedbacks WHERE id = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement feedbackStmt = conn.prepareStatement(feedbackSql)) {

            feedbackStmt.setInt(1, id);

            try (ResultSet rs = feedbackStmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Feedback feedback = mapResultSetToFeedback(rs);
                feedback.setItems(loadItemsByFeedbackId(conn, feedback.getId()));
                return feedback;
            }
        } catch (SQLException e) {
            logger.error("Error while finding feedback with id " + id, e);
            return null;
        }
    }

    @Override
    public List<Feedback> findAllByCustomerId(int customerId) {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = "SELECT * FROM feedbacks WHERE customer_id = ? ORDER BY created_at DESC, id DESC;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Feedback feedback = mapResultSetToFeedback(rs);
                    feedback.setItems(loadItemsByFeedbackId(conn, feedback.getId()));
                    feedbacks.add(feedback);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading feedbacks for customer " + customerId, e);
        }

        return feedbacks;
    }

    @Override
    public List<FeedbackItem> findLowScores(int maxScore) {
        List<FeedbackItem> items = new ArrayList<>();
        String sql = "SELECT * FROM feedback_items WHERE score <= ? ORDER BY score ASC, id ASC;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maxScore);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToFeedbackItem(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading feedback items with score <= {}", maxScore, e);
        }

        return items;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM feedbacks WHERE id = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Feedback with id {} was deleted", id);
            }
        } catch (SQLException e) {
            logger.error("Error while deleting feedback", e);
        }
    }

    private List<FeedbackItem> loadItemsByFeedbackId(Connection conn, int feedbackId) throws SQLException {
        List<FeedbackItem> items = new ArrayList<>();
        String sql = "SELECT * FROM feedback_items WHERE feedback_id = ? ORDER BY id;";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, feedbackId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToFeedbackItem(rs));
                }
            }
        }

        return items;
    }

    private Feedback mapResultSetToFeedback(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setId(rs.getInt("id"));
        feedback.setCustomerId(rs.getInt("customer_id"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            feedback.setCreatedAt(createdAt.toLocalDateTime());
        }

        return feedback;
    }

    private FeedbackItem mapResultSetToFeedbackItem(ResultSet rs) throws SQLException {
        FeedbackItem item = new FeedbackItem();
        item.setId(rs.getInt("id"));
        item.setFeedbackId(rs.getInt("feedback_id"));

        String category = rs.getString("category");
        if (category != null) {
            item.setCategory(FeedbackCategory.valueOf(category));
        }

        item.setScore(rs.getInt("score"));
        item.setComment(rs.getString("comment"));
        return item;
    }

    private void setCreatedAt(PreparedStatement pstmt, int parameterIndex, LocalDateTime createdAt) throws SQLException {
        if (createdAt != null) {
            pstmt.setTimestamp(parameterIndex, Timestamp.valueOf(createdAt));
        } else {
            pstmt.setNull(parameterIndex, Types.TIMESTAMP);
        }
    }

    private int extractGeneratedId(PreparedStatement pstmt, String entityName) throws SQLException {
        try (ResultSet keys = pstmt.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getInt(1);
            }
        }

        throw new SQLException("Could not retrieve generated key for " + entityName);
    }
}
