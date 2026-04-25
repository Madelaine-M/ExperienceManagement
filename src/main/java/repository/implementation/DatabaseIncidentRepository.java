package repository.implementation;

import database.connection.ConnectionProvider;
import database.connection.DatabaseConnectionProvider;
import model.DelayIncident;
import model.FeedbackIncident;
import model.Incident;
import model.enums.IncidentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.implementation.mapper.IncidentResultSetMapper;
import repository.implementation.support.FlightIdResolver;
import repository.implementation.support.GeneratedKeyExtractor;
import repository.interfaces.IncidentLookup;
import repository.interfaces.IncidentManagement;
import repository.interfaces.IncidentUpdate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseIncidentRepository implements IncidentLookup, IncidentUpdate, IncidentManagement {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseIncidentRepository.class);
    private final ConnectionProvider connectionProvider;
    private final IncidentResultSetMapper incidentMapper;
    private final GeneratedKeyExtractor generatedKeyExtractor;
    private final FlightIdResolver flightIdResolver;

    public DatabaseIncidentRepository() {
        this(
                new DatabaseConnectionProvider(),
                new IncidentResultSetMapper(),
                new GeneratedKeyExtractor(),
                new FlightIdResolver()
        );
    }

    public DatabaseIncidentRepository(ConnectionProvider connectionProvider, IncidentResultSetMapper incidentMapper,
                                      GeneratedKeyExtractor generatedKeyExtractor, FlightIdResolver flightIdResolver) {
        this.connectionProvider = connectionProvider;
        this.incidentMapper = incidentMapper;
        this.generatedKeyExtractor = generatedKeyExtractor;
        this.flightIdResolver = flightIdResolver;
    }

    @Override
    public void save(Incident incident) {
        String sql = """
            INSERT INTO incidents (customer_id, type, feedback_id, feedback_type, description, priority_score,
                                   score_impact, revenue_risk, status, assigned_advisor_id,
                                   source_feedback_item_id, delay_minutes, flight_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            Integer resolvedFlightId = flightIdResolver.resolveFlightId(
                    conn,
                    incident.getFlightId(),
                    incident.getCustomerId()
            );

            pstmt.setInt(1, incident.getCustomerId());
            pstmt.setString(2, incident.getType() != null ? incident.getType().name() : null);
            bindFeedbackFields(pstmt, incident);
            pstmt.setString(5, incident.getDescription());
            pstmt.setDouble(6, incident.getPriorityScore());
            pstmt.setDouble(7, incident.getScoreImpact());
            pstmt.setInt(8, incident.getRevenueRisk());
            pstmt.setString(9, incident.getStatus() != null ? incident.getStatus().name() : null);
            if (incident.getAssignedAdvisorId() != null) {
                pstmt.setInt(10, incident.getAssignedAdvisorId());
            } else {
                pstmt.setNull(10, Types.INTEGER);
            }
            bindSourceFeedbackItemId(pstmt, incident);
            bindDelayFields(pstmt, incident);
            if (resolvedFlightId != null) {
                pstmt.setInt(13, resolvedFlightId);
                incident.setFlightId(resolvedFlightId);
            } else {
                pstmt.setNull(13, Types.INTEGER);
            }

            pstmt.executeUpdate();
            incident.setId(generatedKeyExtractor.extractGeneratedId(pstmt, "incident"));
            logger.info("Incident type {} for customer {} saved.", incident.getType(), incident.getCustomerId());

        } catch (SQLException e) {
            logger.error("Error while saving incident", e);
        }
    }

    @Override
    public void updateStatus(int id, IncidentStatus status) {
        String sql = "UPDATE incidents SET status = ? WHERE id = ?;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status != null ? status.name() : null);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while updating incident status for incident {}", id, e);
        }
    }

    @Override
    public List<Incident> findAllByCustomerId(int customerId) {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incidents WHERE customer_id = ?;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                incidents.add(incidentMapper.map(rs));
            }
        } catch (SQLException e) {
            logger.error("Error while loading Incidents for customer " + customerId, e);
        }
        return incidents;
    }

    @Override
    public Incident findById(int id) {
        String sql = "SELECT * FROM incidents WHERE id = ?;";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return incidentMapper.map(rs);
        } catch (SQLException e) {
            logger.error("Error while searching for incidents", e);
        }
        return null;
    }

    @Override
    public List<Incident> findByStatus(IncidentStatus status) {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incidents WHERE status = ?;";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status != null ? status.name() : null);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) incidents.add(incidentMapper.map(rs));
        } catch (SQLException e) {
            logger.error("Error while filtering status", e);
        }
        return incidents;
    }

    @Override
    public List<Incident> findUnassigned() {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incidents WHERE assigned_advisor_id IS NULL;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                incidents.add(incidentMapper.map(rs));
            }
        } catch (SQLException e) {
            logger.error("Error while loading unassigned incidents", e);
        }

        return incidents;
    }

    @Override
    public List<Incident> findByAdvisorId(Integer advisorId) {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incidents WHERE assigned_advisor_id = ?;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (advisorId != null) {
                pstmt.setInt(1, advisorId);
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    incidents.add(incidentMapper.map(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading incidents for advisor {}", advisorId, e);
        }

        return incidents;
    }

    @Override
    public List<Incident> findPendingActionItems() {
        List<Incident> incidents = new ArrayList<>();
        String sql = """
        SELECT i.*
        FROM incidents i
        LEFT JOIN action_items ai ON i.id = ai.incident_id
        WHERE i.status = 'OPEN' 
        AND ai.id IS NULL
        ORDER BY i.id ASC;
        """;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                incidents.add(incidentMapper.map(rs));
            }
        } catch (SQLException e) {
            logger.error("Error while loading pending action items", e);
        }

        return incidents;
    }

    private void bindFeedbackFields(PreparedStatement pstmt, Incident incident) throws SQLException {
        if (incident instanceof FeedbackIncident feedbackIncident) {
            if (feedbackIncident.getFeedbackId() != null) {
                pstmt.setInt(3, feedbackIncident.getFeedbackId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setString(
                    4,
                    feedbackIncident.getFeedbackType() != null ? feedbackIncident.getFeedbackType().name() : null
            );
            return;
        }

        pstmt.setNull(3, Types.INTEGER);
        pstmt.setNull(4, Types.VARCHAR);
    }

    private void bindSourceFeedbackItemId(PreparedStatement pstmt, Incident incident) throws SQLException {
        if (incident instanceof FeedbackIncident feedbackIncident && feedbackIncident.getSourceFeedbackItemId() != null) {
            pstmt.setInt(11, feedbackIncident.getSourceFeedbackItemId());
            return;
        }

        pstmt.setNull(11, Types.INTEGER);
    }

    private void bindDelayFields(PreparedStatement pstmt, Incident incident) throws SQLException {
        if (incident instanceof DelayIncident delayIncident && delayIncident.getDelayMinutes() != null) {
            pstmt.setInt(12, delayIncident.getDelayMinutes());
            return;
        }

        pstmt.setNull(12, Types.INTEGER);
    }

}
