package repository.implementation.mapper;

import model.Incident;
import model.enums.FeedbackCategory;
import model.enums.IncidentStatus;
import model.enums.IncidentType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class IncidentResultSetMapper {

    public Incident map(ResultSet rs) throws SQLException {
        Incident incident = new Incident();
        incident.setId(rs.getInt("id"));
        incident.setCustomerId(rs.getInt("customer_id"));

        String typeStr = rs.getString("type");
        if (typeStr != null) {
            incident.setType(IncidentType.valueOf(typeStr));
        }

        String feedbackTypeStr = rs.getString("feedback_type");
        if (feedbackTypeStr != null) {
            incident.setFeedbackType(FeedbackCategory.valueOf(feedbackTypeStr));
        }

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

        incident.setFlightId(rs.getInt("flight_id"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            incident.setCreatedAt(createdAt.toLocalDateTime());
        }

        return incident;
    }
}
