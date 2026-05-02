package repository.implementation;

import database.connection.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.RepositoryException;
import repository.interfaces.NPSScores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseNPSScores implements NPSScores {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseNPSScores.class);
    private final ConnectionProvider connectionProvider;

    public DatabaseNPSScores(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public int countByScoreRange(int min, int max) {
        String sql = "SELECT COUNT(*) AS feedback_count FROM feedbacks WHERE referral_score BETWEEN ? AND ?;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, min);
            pstmt.setInt(2, max);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("feedback_count");
                }
            }
        } catch (SQLException e) {
            logger.error("Error while counting feedbacks for score range {} to {}", min, max, e);
            throw new RepositoryException("Failed to count feedbacks for score range " + min + " to " + max, e);
        }

        return 0;
    }
}
