package repository.implementation.support;

import model.domain.Flight;
import repository.implementation.mapper.FlightResultSetMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FlightViewLoader {
    private final FlightResultSetMapper flightMapper;

    public FlightViewLoader() {
        this(new FlightResultSetMapper());
    }

    public FlightViewLoader(FlightResultSetMapper flightMapper) {
        this.flightMapper = flightMapper;
    }

    public Flight loadCurrentFlight(Connection conn, int customerId) throws SQLException {
        String sql = """
            SELECT *
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
                    return flightMapper.map(rs);
                }
            }
        }

        return null;
    }

    public List<Flight> loadPreviousFlights(Connection conn, int customerId) throws SQLException {
        List<Flight> previousFlights = new ArrayList<>();
        String sql = """
            SELECT *
            FROM flights
            WHERE customer_id = ?
              AND is_current = 0
            ORDER BY flight_date DESC, id DESC;
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    previousFlights.add(flightMapper.map(rs));
                }
            }
        }

        return previousFlights;
    }

    public List<Flight> loadPreviousFlights(Connection conn, int customerId, int currentFlightId) throws SQLException {
        List<Flight> previousFlights = new ArrayList<>();
        String sql = """
            SELECT *
            FROM flights
            WHERE customer_id = ?
              AND is_current = 0
              AND id <> ?
            ORDER BY flight_date DESC, id DESC;
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, currentFlightId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    previousFlights.add(flightMapper.map(rs));
                }
            }
        }

        return previousFlights;
    }
}
