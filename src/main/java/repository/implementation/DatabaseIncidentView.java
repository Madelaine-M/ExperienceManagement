package repository.implementation;

import database.connection.DatabaseManager;
import model.Flight;
import model.IncidentDetailView;
import model.IncidentOverview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.implementation.mapper.IncidentViewMapper;
import repository.implementation.support.FlightViewLoader;
import repository.implementation.support.PreviousFlightsSummaryFormatter;
import repository.interfaces.IncidentView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseIncidentView implements IncidentView {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseIncidentView.class);
    private final IncidentViewMapper incidentViewMapper;
    private final FlightViewLoader flightViewLoader;
    private final PreviousFlightsSummaryFormatter previousFlightsSummaryFormatter;

    public DatabaseIncidentView() {
        this(new IncidentViewMapper(), new FlightViewLoader(), new PreviousFlightsSummaryFormatter());
    }

    public DatabaseIncidentView(IncidentViewMapper incidentViewMapper, FlightViewLoader flightViewLoader,
                                PreviousFlightsSummaryFormatter previousFlightsSummaryFormatter) {
        this.incidentViewMapper = incidentViewMapper;
        this.flightViewLoader = flightViewLoader;
        this.previousFlightsSummaryFormatter = previousFlightsSummaryFormatter;
    }

    @Override
    public List<IncidentOverview> findAllPrioritizedOverviews(int advisorId) {
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
              AND i.assigned_advisor_id = ?
            ORDER BY i.priority_score DESC, i.created_at ASC;
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, advisorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    overviews.add(incidentViewMapper.mapOverview(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading prioritized incident overviews for advisor {}", advisorId, e);
        }

        return overviews;
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
                    IncidentDetailView detail = incidentViewMapper.mapDetail(rs);
                    List<Flight> previousFlights = flightViewLoader.loadPreviousFlights(
                            conn,
                            detail.getCustomerId(),
                            detail.getCurrentFlightId()
                    );
                    detail.setPreviousFlightsList(previousFlights);
                    detail.setPreviousFlights(previousFlightsSummaryFormatter.format(previousFlights));
                    return detail;
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading incident detail view for incident {}", incidentId, e);
        }

        return null;
    }
}
