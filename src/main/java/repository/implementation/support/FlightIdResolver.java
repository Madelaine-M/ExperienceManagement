package repository.implementation.support;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FlightIdResolver {

    public Integer resolveFlightId(Connection conn, int requestedFlightId, int customerId) throws SQLException {
        if (requestedFlightId > 0 && flightExists(conn, requestedFlightId)) {
            return requestedFlightId;
        }

        return findCurrentFlightIdByCustomerId(conn, customerId);
    }

    private boolean flightExists(Connection conn, int flightId) throws SQLException {
        String sql = "SELECT 1 FROM flights WHERE id = ? LIMIT 1;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, flightId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Integer findCurrentFlightIdByCustomerId(Connection conn, int customerId) throws SQLException {
        String sql = """
            SELECT id
            FROM flights
            WHERE customer_id = ?
              AND is_current = 1
            ORDER BY id DESC
            LIMIT 1;
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        return null;
    }
}
