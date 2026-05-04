package repository.implementation;

import database.connection.ConnectionProvider;
import model.domain.DelayIncident;
import model.domain.FeedbackIncident;
import model.domain.Incident;
import model.enums.IncidentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.RepositoryException;
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
            INSERT INTO incidents (customer_id, type, feedback_id, feedback_type, description,
                                   score_impact, revenue_risk, status, assigned_advisor_id,
                                   source_feedback_item_id, delay_minutes, flight_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
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
            pstmt.setInt(6, incident.getScoreImpact());
            pstmt.setInt(7, incident.getRevenueRisk());
            pstmt.setString(8, incident.getStatus() != null ? incident.getStatus().name() : null);
            if (incident.getAssignedAdvisorId() != null) {
                pstmt.setInt(9, incident.getAssignedAdvisorId());
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }
            bindSourceFeedbackItemId(pstmt, incident);
            bindDelayMinutes(pstmt, incident);
            if (resolvedFlightId != null) {
                pstmt.setInt(12, resolvedFlightId);
                incident.setFlightId(resolvedFlightId);
            } else {
                pstmt.setNull(12, Types.INTEGER);
            }

            pstmt.executeUpdate();
            incident.setId(generatedKeyExtractor.extractGeneratedId(pstmt, "incident"));
            logger.info("Incident type {} for customer {} saved.", incident.getType(), incident.getCustomerId());

        } catch (SQLException e) {
            logger.error("Error while saving incident", e);
            throw new RepositoryException("Failed to save incident for customer " + incident.getCustomerId(), e);
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
            throw new RepositoryException("Failed to update incident status for incident " + id, e);
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
            throw new RepositoryException("Failed to load incidents for customer " + customerId, e);
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
            throw new RepositoryException("Failed to find incident " + id, e);
        }
        return null;
    }

    @Override
    public boolean existsByFeedbackId(int feedbackId) {
        String sql = """
            SELECT 1
            FROM incidents
            WHERE feedback_id = ?
            LIMIT 1;
            """;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, feedbackId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error while checking incidents for feedback {}", feedbackId, e);
            throw new RepositoryException("Failed to check incidents for feedback " + feedbackId, e);
        }

    }

    @Override
    public boolean existsBySourceFeedbackItemId(int sourceFeedbackItemId) {
        String sql = """
            SELECT 1
            FROM incidents
            WHERE source_feedback_item_id = ?
            LIMIT 1;
            """;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sourceFeedbackItemId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error while checking incidents for feedback item {}", sourceFeedbackItemId, e);
            throw new RepositoryException("Failed to check incidents for feedback item " + sourceFeedbackItemId, e);
        }

    }

    @Override
    public boolean existsOpenDelayIncidentForCustomerFlight(int customerId, Integer flightId) {
        String sql = """
            SELECT 1
            FROM incidents
            WHERE customer_id = ?
              AND type = 'DELAY'
              AND status = 'OPEN'
              AND (
                    (flight_id = ?)
                    OR (flight_id IS NULL AND ? IS NULL)
              )
            LIMIT 1;
            """;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            if (flightId != null) {
                pstmt.setInt(2, flightId);
                pstmt.setInt(3, flightId);
            } else {
                pstmt.setNull(2, Types.INTEGER);
                pstmt.setNull(3, Types.INTEGER);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error while checking open delay incidents for customer {} and flight {}", customerId, flightId, e);
            throw new RepositoryException("Failed to check open delay incidents for customer " + customerId, e);
        }

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
            throw new RepositoryException("Failed to find incidents by status " + status, e);
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
            throw new RepositoryException("Failed to load unassigned incidents", e);
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
            throw new RepositoryException("Failed to load incidents for advisor " + advisorId, e);
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
            throw new RepositoryException("Failed to load pending-action incidents", e);
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
            pstmt.setInt(10, feedbackIncident.getSourceFeedbackItemId());
            return;
        }

        pstmt.setNull(10, Types.INTEGER);
    }

    private void bindDelayMinutes(PreparedStatement pstmt, Incident incident) throws SQLException {
        if (incident instanceof DelayIncident delayIncident && delayIncident.getDelayMinutes() != null) {
            pstmt.setInt(11, delayIncident.getDelayMinutes());
            return;
        }

        pstmt.setNull(11, Types.INTEGER);
    }

}
