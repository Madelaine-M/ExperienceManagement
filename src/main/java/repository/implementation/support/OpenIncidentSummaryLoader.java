package repository.implementation.support;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OpenIncidentSummaryLoader {

    public OpenIncidentSummary loadForCustomer(Connection conn, int customerId) throws SQLException {
        String sql = """
            SELECT id,
                   priority_score,
                   description
            FROM incidents
            WHERE customer_id = ?
              AND status = 'OPEN'
            ORDER BY priority_score DESC, created_at ASC, id ASC
            LIMIT 1;
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new OpenIncidentSummary(
                            true,
                            rs.getInt("id"),
                            rs.getDouble("priority_score"),
                            rs.getString("description")
                    );
                }
            }
        }

        return new OpenIncidentSummary(false, null, null, null);
    }
}
