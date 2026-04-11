package repository.implementation;

import database.connection.DatabaseManager;
import model.SentimentHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.interfaces.HistoryRepository;

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

public class DatabaseHistoryRepository implements HistoryRepository {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseHistoryRepository.class);

    @Override
    public void save(SentimentHistory historyEntry) {
        String sql = """
            INSERT INTO sentiment_history (customer_id, recorded_at, clv_score, score_impact, feedback_id)
            VALUES (?, ?, ?, ?, ?);
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, historyEntry.getCustomerId());
            setRecordedAt(pstmt, 2, historyEntry.getRecordedAt());
            pstmt.setDouble(3, historyEntry.getClvScore());
            pstmt.setDouble(4, historyEntry.getScoreImpact());
            if (historyEntry.getFeedbackId() > 0) {
                pstmt.setInt(5, historyEntry.getFeedbackId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    historyEntry.setId(generatedKeys.getInt(1));
                }
            }

            logger.info("Sentiment history saved for customer {}", historyEntry.getCustomerId());
        } catch (SQLException e) {
            logger.error("Error while saving sentiment history", e);
        }
    }

    @Override
    public SentimentHistory findById(int id) {
        String sql = "SELECT * FROM sentiment_history WHERE id = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSentimentHistory(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while finding sentiment history with id " + id, e);
        }

        return null;
    }

    @Override
    public List<SentimentHistory> findByCustomerId(int customerId) {
        List<SentimentHistory> historyEntries = new ArrayList<>();
        String sql = "SELECT * FROM sentiment_history WHERE customer_id = ? ORDER BY recorded_at ASC, id ASC;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    historyEntries.add(mapResultSetToSentimentHistory(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading sentiment history for customer " + customerId, e);
        }

        return historyEntries;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM sentiment_history WHERE id = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while deleting sentiment history", e);
        }
    }

    private SentimentHistory mapResultSetToSentimentHistory(ResultSet rs) throws SQLException {
        SentimentHistory history = new SentimentHistory();
        history.setId(rs.getInt("id"));
        history.setCustomerId(rs.getInt("customer_id"));

        Timestamp recordedAt = rs.getTimestamp("recorded_at");
        if (recordedAt != null) {
            history.setRecordedAt(recordedAt.toLocalDateTime());
        }

        history.setClvScore(rs.getDouble("clv_score"));
        history.setScoreImpact(rs.getDouble("score_impact"));
        history.setFeedbackId(rs.getInt("feedback_id"));
        return history;
    }

    private void setRecordedAt(PreparedStatement pstmt, int parameterIndex, LocalDateTime recordedAt) throws SQLException {
        if (recordedAt != null) {
            pstmt.setTimestamp(parameterIndex, Timestamp.valueOf(recordedAt));
        } else {
            pstmt.setNull(parameterIndex, Types.TIMESTAMP);
        }
    }
}
