package repository.implementation.mapper;

import model.view.IncidentDetailView;
import model.view.IncidentOverview;
import model.enums.CustomerStatus;
import model.enums.IncidentType;
import model.enums.Packages;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IncidentViewMapper {

    public IncidentOverview mapOverview(ResultSet rs) throws SQLException {
        IncidentOverview overview = new IncidentOverview();
        overview.setIncidentId(rs.getInt("id"));
        overview.setCustomerId(rs.getInt("customer_id"));
        overview.setCustomerFirstName(rs.getString("first_name"));
        overview.setCustomerLastName(rs.getString("last_name"));
        String customerStatusStr = rs.getString("customer_status");
        if (customerStatusStr != null) {
            overview.setCustomerStatus(CustomerStatus.valueOf(customerStatusStr));
        }
        overview.setDescription(rs.getString("description"));
        overview.setRevenueRisk(rs.getInt("revenue_risk"));
        overview.setScoreImpact(rs.getInt("score_impact"));
        java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            overview.setCreatedAt(createdAt.toLocalDateTime());
        }

        String packageStr = rs.getString("booking_package");
        if (packageStr != null) {
            overview.setBookingPackage(Packages.valueOf(packageStr));
        }

        String incidentTypeStr = rs.getString("type");
        if (incidentTypeStr != null) {
            overview.setIncidentType(IncidentType.valueOf(incidentTypeStr));
        }

        return overview;
    }

    public IncidentDetailView mapDetail(ResultSet rs) throws SQLException {
        IncidentDetailView detail = new IncidentDetailView();
        detail.setIncidentId(rs.getInt("id"));
        detail.setCustomerId(rs.getInt("customer_id"));
        detail.setCustomerFirstName(rs.getString("first_name"));
        detail.setCustomerLastName(rs.getString("last_name"));
        String customerStatusStr = rs.getString("customer_status");
        if (customerStatusStr != null) {
            detail.setCustomerStatus(CustomerStatus.valueOf(customerStatusStr));
        }
        detail.setReturning(rs.getBoolean("is_returning"));
        detail.setCurrentFlightDate(rs.getString("flight_date"));
        int currentFlightId = rs.getInt("flight_id");
        if (!rs.wasNull()) {
            detail.setCurrentFlightId(currentFlightId);
        }
        detail.setCurrentFlightNumber(rs.getString("flight_number"));
        detail.setIncidentDescription(rs.getString("description"));

        String bookingPackageStr = rs.getString("booking_package");
        if (bookingPackageStr != null) {
            detail.setBookingPackage(Packages.valueOf(bookingPackageStr));
        }

        String incidentTypeStr = rs.getString("type");
        if (incidentTypeStr != null) {
            detail.setIncidentType(IncidentType.valueOf(incidentTypeStr));
        }

        int feedbackId = rs.getInt("feedback_id");
        if (!rs.wasNull()) {
            detail.setFeedbackId(feedbackId);
        }

        int sourceFeedbackItemId = rs.getInt("source_feedback_item_id");
        if (!rs.wasNull()) {
            detail.setSourceFeedbackItemId(sourceFeedbackItemId);
        }

        int delayMinutes = rs.getInt("delay_minutes");
        if (detail.getIncidentType() == IncidentType.DELAY && !rs.wasNull()) {
            detail.setDelayMinutes(delayMinutes);
        }

        return detail;
    }
}
