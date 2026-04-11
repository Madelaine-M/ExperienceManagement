package repository.implementation;

import database.connection.DatabaseManager;
import model.Incident;
import model.enums.FeedbackCategory;
import model.enums.IncidentStatus;
import model.enums.IncidentType;
import repository.interfaces.IncidentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseIncidentRepository implements IncidentRepository {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseIncidentRepository.class);

    @Override
    public void save(Incident incident) {
        String sql = """
            INSERT INTO incidents (customer_id, type, feedback_type, description, priority_score, score_impact, revenue_risk, status, assigned_advisor_id, source_feedback_item_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, incident.getCustomerId());
            pstmt.setString(2, incident.getType() != null ? incident.getType().name() : null);
            pstmt.setString(3, incident.getFeedbackType() != null ? incident.getFeedbackType().name() : null);
            pstmt.setString(4, incident.getDescription());
            pstmt.setDouble(5, incident.getPriorityScore());
            pstmt.setDouble(6, incident.getScoreImpact());
            pstmt.setInt(7, incident.getRevenueRisk());
            pstmt.setString(8, incident.getStatus() != null ? incident.getStatus().name() : null);
            if (incident.getAssignedAdvisorId() != null) {
                pstmt.setInt(9, incident.getAssignedAdvisorId());
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }
            if (incident.getSourceFeedbackItemId() != null) {
                pstmt.setInt(10, incident.getSourceFeedbackItemId());
            } else {
                pstmt.setNull(10, Types.INTEGER);
            }

            pstmt.executeUpdate();
            logger.info("Incident type {} for customer {} saved.", incident.getType(), incident.getCustomerId());

        } catch (SQLException e) {
            logger.error("Error while saving incident", e);
        }
    }

    @Override
    public List<Incident> findAllByCustomerId(int customerId) {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incidents WHERE customer_id = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                incidents.add(mapResultSetToIncident(rs));
            }
        } catch (SQLException e) {
            logger.error("Error while loading Incidents for customer " + customerId, e);
        }
        return incidents;
    }

    @Override
    public Incident findById(int id) {
        String sql = "SELECT * FROM incidents WHERE id = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapResultSetToIncident(rs);
        } catch (SQLException e) {
            logger.error("Error while searching for incidents", e);
        }
        return null;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM incidents WHERE id = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while deleting incidents", e);
        }
    }

    @Override
    public List<Incident> findByStatus(IncidentStatus status) {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incidents WHERE status = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status != null ? status.name() : null);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) incidents.add(mapResultSetToIncident(rs));
        } catch (SQLException e) {
            logger.error("Error while filtering status", e);
        }
        return incidents;
    }

    @Override
    public List<Incident> findUnassigned() {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incidents WHERE assigned_advisor_id IS NULL;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                incidents.add(mapResultSetToIncident(rs));
            }
        } catch (SQLException e) {
            logger.error("Error while loading unassigned incidents", e);
        }

        return incidents;
    }

    @Override
    public List<Incident> findByAdvisorId(Long advisorId) {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incidents WHERE assigned_advisor_id = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (advisorId != null) {
                pstmt.setLong(1, advisorId);
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    incidents.add(mapResultSetToIncident(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading incidents for advisor {}", advisorId, e);
        }

        return incidents;
    }

    // Hilfsmethode für das Mapping (SRP!)
    private Incident mapResultSetToIncident(ResultSet rs) throws SQLException {
        Incident incident = new Incident();
        incident.setId(rs.getInt("id"));
        incident.setCustomerId(rs.getInt("customer_id"));

        String typeStr = rs.getString("type");
        if (typeStr != null) incident.setType(IncidentType.valueOf(typeStr));
        String feedbackTypeStr = rs.getString("feedback_type");
        if (feedbackTypeStr != null) incident.setFeedbackType(FeedbackCategory.valueOf(feedbackTypeStr));

        incident.setDescription(rs.getString("description"));
        incident.setPriorityScore(rs.getDouble("priority_score"));
        incident.setScoreImpact(rs.getDouble("score_impact"));
        incident.setRevenueRisk(rs.getInt("revenue_risk"));
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            incident.setStatus(IncidentStatus.valueOf(statusStr));
        }
        int assignedAdvisorId = rs.getInt("assigned_advisor_id");
        if (!rs.wasNull()) {
            incident.setAssignedAdvisorId(assignedAdvisorId);
        }
        int sourceFeedbackItemId = rs.getInt("source_feedback_item_id");
        if (!rs.wasNull()) {
            incident.setSourceFeedbackItemId(sourceFeedbackItemId);
        }

        // Timestamp umwandeln (SQLite speichert das als String)
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) incident.setCreatedAt(ts.toLocalDateTime());

        return incident;
    }
}
