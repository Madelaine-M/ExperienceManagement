package repository.implementation;

import database.connection.ConnectionProvider;
import database.connection.DatabaseConnectionProvider;
import model.Feedback;
import model.FeedbackItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.implementation.support.FlightIdResolver;
import repository.implementation.support.GeneratedKeyExtractor;
import repository.interfaces.FeedbackUpdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

public class DatabaseFeedbackUpdate implements FeedbackUpdate {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseFeedbackUpdate.class);
    private final ConnectionProvider connectionProvider;
    private final GeneratedKeyExtractor generatedKeyExtractor;
    private final FlightIdResolver flightIdResolver;

    public DatabaseFeedbackUpdate() {
        this(new DatabaseConnectionProvider(), new GeneratedKeyExtractor(), new FlightIdResolver());
    }

    public DatabaseFeedbackUpdate(ConnectionProvider connectionProvider, GeneratedKeyExtractor generatedKeyExtractor,
                                  FlightIdResolver flightIdResolver) {
        this.connectionProvider = connectionProvider;
        this.generatedKeyExtractor = generatedKeyExtractor;
        this.flightIdResolver = flightIdResolver;
    }

    @Override
    public void save(Feedback feedback) {
        String feedbackSql = """
            INSERT INTO feedbacks (customer_id, created_at, total_score, customer_sat_score, flight_id)
            VALUES (?, ?, ?, ?, ?);
            """;
        String itemSql = """
            INSERT INTO feedback_items (feedback_id, category, score, comment)
            VALUES (?, ?, ?, ?);
            """;

        try (Connection conn = connectionProvider.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement feedbackStmt = conn.prepareStatement(feedbackSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement itemStmt = conn.prepareStatement(itemSql, Statement.RETURN_GENERATED_KEYS)) {
                Integer resolvedFlightId = flightIdResolver.resolveFlightId(
                        conn,
                        feedback.getFlightId(),
                        feedback.getCustomerId()
                );

                feedbackStmt.setInt(1, feedback.getCustomerId());
                setCreatedAt(feedbackStmt, 2, feedback.getCreatedAt());
                feedbackStmt.setDouble(3, feedback.getTotalScore() != 0.0 ? feedback.getTotalScore() : feedback.getOverallScore());
                feedbackStmt.setInt(4, feedback.getCustomerSatScore());
                if (resolvedFlightId != null) {
                    feedbackStmt.setInt(5, resolvedFlightId);
                    feedback.setFlightId(resolvedFlightId);
                } else {
                    feedbackStmt.setNull(5, Types.INTEGER);
                }
                feedbackStmt.executeUpdate();

                int feedbackId = generatedKeyExtractor.extractGeneratedId(feedbackStmt, "feedback");
                feedback.setId(feedbackId);

                for (FeedbackItem item : feedback.getItems()) {
                    itemStmt.setInt(1, feedbackId);
                    itemStmt.setString(2, item.getCategory() != null ? item.getCategory().name() : null);
                    itemStmt.setInt(3, item.getScore());
                    itemStmt.setString(4, item.getComment());
                    itemStmt.executeUpdate();

                    int itemId = generatedKeyExtractor.extractGeneratedId(itemStmt, "feedback item");
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

    private void setCreatedAt(PreparedStatement pstmt, int parameterIndex, LocalDateTime createdAt) throws SQLException {
        if (createdAt != null) {
            pstmt.setTimestamp(parameterIndex, Timestamp.valueOf(createdAt));
        } else {
            pstmt.setNull(parameterIndex, Types.TIMESTAMP);
        }
    }
}
