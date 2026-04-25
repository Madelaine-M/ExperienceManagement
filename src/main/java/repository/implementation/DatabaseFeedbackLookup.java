package repository.implementation;

import database.connection.ConnectionProvider;
import database.connection.DatabaseConnectionProvider;
import model.Feedback;
import model.FeedbackItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.implementation.mapper.FeedbackResultSetMapper;
import repository.implementation.support.FeedbackItemLoader;
import repository.interfaces.FeedbackLookup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFeedbackLookup implements FeedbackLookup {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseFeedbackLookup.class);
    private final ConnectionProvider connectionProvider;
    private final FeedbackResultSetMapper feedbackMapper;
    private final FeedbackItemLoader feedbackItemLoader;

    public DatabaseFeedbackLookup() {
        this(new DatabaseConnectionProvider(), new FeedbackResultSetMapper(), new FeedbackItemLoader());
    }

    public DatabaseFeedbackLookup(ConnectionProvider connectionProvider, FeedbackResultSetMapper feedbackMapper,
                                  FeedbackItemLoader feedbackItemLoader) {
        this.connectionProvider = connectionProvider;
        this.feedbackMapper = feedbackMapper;
        this.feedbackItemLoader = feedbackItemLoader;
    }

    @Override
    public Feedback findById(int id) {
        String feedbackSql = "SELECT * FROM feedbacks WHERE id = ?;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement feedbackStmt = conn.prepareStatement(feedbackSql)) {

            feedbackStmt.setInt(1, id);

            try (ResultSet rs = feedbackStmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Feedback feedback = feedbackMapper.map(rs);
                feedback.setItems(feedbackItemLoader.loadItemsByFeedbackId(conn, feedback.getId()));
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

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Feedback feedback = feedbackMapper.map(rs);
                    feedback.setItems(feedbackItemLoader.loadItemsByFeedbackId(conn, feedback.getId()));
                    feedbacks.add(feedback);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading feedbacks for customer " + customerId, e);
        }

        return feedbacks;
    }

    @Override
    public List<Feedback> findAllByFlightId(int flightId) {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = "SELECT * FROM feedbacks WHERE flight_id = ? ORDER BY created_at DESC, id DESC;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, flightId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Feedback feedback = feedbackMapper.map(rs);
                    feedback.setItems(feedbackItemLoader.loadItemsByFeedbackId(conn, feedback.getId()));
                    feedbacks.add(feedback);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading feedbacks for flight {}", flightId, e);
        }

        return feedbacks;
    }

    @Override
    public List<FeedbackItem> findItemsByFeedbackId(int id) {
        try (Connection conn = connectionProvider.getConnection()) {
            return feedbackItemLoader.loadItemsByFeedbackId(conn, id);
        } catch (SQLException e) {
            logger.error("Error while loading feedback items for feedback {}", id, e);
            return new ArrayList<>();
        }
    }
}
