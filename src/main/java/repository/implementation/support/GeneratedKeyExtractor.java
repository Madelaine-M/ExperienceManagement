package repository.implementation.support;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// AI used
public class GeneratedKeyExtractor {

    public int extractGeneratedId(PreparedStatement pstmt, String entityName) throws SQLException {
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }

        throw new SQLException("Could not retrieve generated key for " + entityName);
    }
}
