package repository.implementation.mapper;

import model.domain.DelayIncident;
import model.domain.FeedbackIncident;
import model.domain.Incident;
import model.enums.FeedbackCategory;
import model.enums.IncidentStatus;
import model.enums.IncidentType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class IncidentResultSetMapper {

    public Incident map(ResultSet rs) throws SQLException {
        String typeStr = rs.getString("type");
        IncidentType incidentType = typeStr != null ? IncidentType.valueOf(typeStr) : null;

        Incident incident = createIncidentSubtype(incidentType);
        incident.setId(rs.getInt("id"));
        incident.setCustomerId(rs.getInt("customer_id"));

        if (incident instanceof FeedbackIncident feedbackIncident) {
            String feedbackTypeStr = rs.getString("feedback_type");
            if (feedbackTypeStr != null) {
                feedbackIncident.setFeedbackType(FeedbackCategory.valueOf(feedbackTypeStr));
            }

            int feedbackId = rs.getInt("feedback_id");
            if (!rs.wasNull()) {
                feedbackIncident.setFeedbackId(feedbackId);
            }

            int sourceFeedbackItemId = rs.getInt("source_feedback_item_id");
            if (!rs.wasNull()) {
                feedbackIncident.setSourceFeedbackItemId(sourceFeedbackItemId);
            }
        }
        if (incident instanceof DelayIncident delayIncident) {
            int delayMinutes = rs.getInt("delay_minutes");
            if (!rs.wasNull()) {
                delayIncident.setDelayMinutes(delayMinutes);
            }
        }
        incident.setDescription(rs.getString("description"));
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

        int flightId = rs.getInt("flight_id");
        if (!rs.wasNull()) {
            incident.setFlightId(flightId);
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            incident.setCreatedAt(createdAt.toLocalDateTime());
        }

        return incident;
    }

    private Incident createIncidentSubtype(IncidentType incidentType) {
        if (incidentType == null) {
            throw new IllegalArgumentException("Incident type must not be null");
        }

        return switch (incidentType) {
            case FEEDBACK -> new FeedbackIncident();
            case DELAY -> new DelayIncident();
        };
    }
}
