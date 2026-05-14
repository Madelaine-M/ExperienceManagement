package repository.implementation.mapper;

import model.domain.Customer;
import model.enums.CustomerStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

//AI was used to implement the idea of mapping between database to object
public class CustomerResultSetMapper {

    public Customer map(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setEmail(rs.getString("email"));
        customer.setBirthDate(rs.getString("birth_date"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            customer.setStatus(CustomerStatus.valueOf(statusStr));
        }

        customer.setReturning(rs.getBoolean("is_returning"));
        int assignedAdvisorId = rs.getInt("assigned_advisor_id");
        if (!rs.wasNull()) {
            customer.setAssignedAdvisorId(assignedAdvisorId);
        }
        customer.setPreferences(rs.getString("preferences"));
        customer.setApplyToNextBooking(rs.getString("apply_to_next_booking"));
        Timestamp statusUpdatedAt = rs.getTimestamp("status_updated_at");
        if (statusUpdatedAt != null) {
            customer.setStatusUpdatedAt(statusUpdatedAt.toLocalDateTime());
        }
        return customer;
    }
}
