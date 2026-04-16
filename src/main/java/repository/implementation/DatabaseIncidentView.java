package repository.implementation;

import database.connection.DatabaseManager;
import model.Flight;
import model.IncidentDetailView;
import model.IncidentOverview;
import model.enums.CustomerType;
import model.enums.IncidentType;
import model.enums.Packages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.interfaces.IncidentView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseIncidentView implements IncidentView {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseIncidentView.class);

    @Override
    public List<IncidentOverview> findAllPrioritizedOverviews() {
        List<IncidentOverview> overviews = new ArrayList<>();
        String sql = """
            SELECT i.id,
                   i.customer_id,
                   c.first_name,
                   c.last_name,
                   f.booking_package,
                   i.priority_score,
                   i.description,
                   i.revenue_risk,
                   i.score_impact
            FROM incidents i
            JOIN customers c ON i.customer_id = c.id
            LEFT JOIN flights f ON i.flight_id = f.id
            WHERE i.status = 'OPEN'
            ORDER BY i.priority_score DESC, i.created_at ASC;
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                overviews.add(mapResultSetToOverview(rs));
            }
        } catch (SQLException e) {
            logger.error("Error while loading prioritized incident overviews", e);
        }

        return overviews;
    }

    private IncidentOverview mapResultSetToOverview(ResultSet rs) throws SQLException {
        IncidentOverview overview = new IncidentOverview();
        overview.setIncidentId(rs.getInt("id"));
        overview.setCustomerId(rs.getInt("customer_id"));
        overview.setCustomerFirstName(rs.getString("first_name"));
        overview.setCustomerLastName(rs.getString("last_name"));
        overview.setDescription(rs.getString("description"));
        overview.setPriorityScore(rs.getDouble("priority_score"));
        overview.setRevenueRisk(rs.getInt("revenue_risk"));
        overview.setScoreImpact(rs.getDouble("score_impact"));

        String packageStr = rs.getString("booking_package");
        if (packageStr != null) {
            overview.setBookingPackage(Packages.valueOf(packageStr));
        }

        return overview;
    }

    @Override
    public IncidentDetailView findDetailByIncidentId(int incidentId) {
        String sql = """
            SELECT i.id,
                   i.customer_id,
                   i.type,
                   i.description,
                   c.first_name,
                   c.last_name,
                    c.is_returning,
                   c.customer_type,
                   f.id AS flight_id,
                   f.flight_number,
                   f.flight_date,
                   f.booking_package
            FROM incidents i
            JOIN customers c ON i.customer_id = c.id
            LEFT JOIN flights f ON i.flight_id = f.id
            WHERE i.id = ?;
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, incidentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDetail(conn, rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading incident detail view for incident {}", incidentId, e);
        }

        return null;
    }

    private IncidentDetailView mapResultSetToDetail(Connection conn, ResultSet rs) throws SQLException {
        IncidentDetailView detail = new IncidentDetailView();
        detail.setIncidentId(rs.getInt("id"));
        detail.setCustomerId(rs.getInt("customer_id"));
        detail.setCustomerFirstName(rs.getString("first_name"));
        detail.setCustomerLastName(rs.getString("last_name"));
        detail.setReturning(rs.getBoolean("is_returning"));
        detail.setCurrentFlightDate(rs.getString("flight_date"));
        detail.setCurrentFlightId(rs.getInt("flight_id"));
        detail.setCurrentFlightNumber(rs.getString("flight_number"));
        detail.setIncidentDescription(rs.getString("description"));

        String bookingPackageStr = rs.getString("booking_package");
        if (bookingPackageStr != null) {
            detail.setBookingPackage(Packages.valueOf(bookingPackageStr));
        }

        String customerTypeStr = rs.getString("customer_type");
        if (customerTypeStr != null) {
            detail.setCustomerType(CustomerType.valueOf(customerTypeStr));
        }

        String incidentTypeStr = rs.getString("type");
        if (incidentTypeStr != null) {
            detail.setIncidentType(IncidentType.valueOf(incidentTypeStr));
        }

        List<Flight> previousFlights = loadPreviousFlights(conn, detail.getCustomerId(), detail.getCurrentFlightId());
        detail.setPreviousFlightsList(previousFlights);
        detail.setPreviousFlights(buildPreviousFlightsSummary(previousFlights));

        return detail;
    }

    private List<Flight> loadPreviousFlights(Connection conn, int customerId, int currentFlightId) throws SQLException {
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
                    previousFlights.add(mapResultSetToFlight(rs));
                }
            }
        }

        return previousFlights;
    }

    private Flight mapResultSetToFlight(ResultSet rs) throws SQLException {
        Flight flight = new Flight();
        flight.setId(rs.getInt("id"));
        flight.setCustomerId(rs.getInt("customer_id"));
        flight.setFlightNumber(rs.getString("flight_number"));
        flight.setFlightDate(rs.getString("flight_date"));
        flight.setStatus(rs.getString("status"));
        flight.setCurrent(rs.getBoolean("is_current"));

        String bookingPackage = rs.getString("booking_package");
        if (bookingPackage != null) {
            flight.setBookingPackage(Packages.valueOf(bookingPackage));
        }

        return flight;
    }

    private String buildPreviousFlightsSummary(List<Flight> previousFlights) {
        if (previousFlights.isEmpty()) {
            return "No previous flight data";
        }

        return previousFlights.size() + " previous flight(s)";
    }
}
