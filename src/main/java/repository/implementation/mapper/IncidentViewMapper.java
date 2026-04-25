package repository.implementation.mapper;

import model.IncidentDetailView;
import model.IncidentOverview;
import model.enums.CustomerType;
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

    public IncidentDetailView mapDetail(ResultSet rs) throws SQLException {
        IncidentDetailView detail = new IncidentDetailView();
        detail.setIncidentId(rs.getInt("id"));
        detail.setCustomerId(rs.getInt("customer_id"));
        detail.setCustomerFirstName(rs.getString("first_name"));
        detail.setCustomerLastName(rs.getString("last_name"));
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

        String customerTypeStr = rs.getString("customer_type");
        if (customerTypeStr != null) {
            detail.setCustomerType(CustomerType.valueOf(customerTypeStr));
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
        if (!rs.wasNull()) {
            detail.setDelayMinutes(delayMinutes);
        }

        return detail;
    }
}
