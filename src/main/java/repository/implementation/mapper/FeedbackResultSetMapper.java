package repository.implementation.mapper;

import model.Feedback;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class FeedbackResultSetMapper {

    public Feedback map(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setId(rs.getInt("id"));
        feedback.setCustomerId(rs.getInt("customer_id"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            feedback.setCreatedAt(createdAt.toLocalDateTime());
        }

        feedback.setTotalScore(rs.getDouble("total_score"));
        feedback.setCustomerSatScore(rs.getInt("customer_sat_score"));
        feedback.setFlightId(rs.getInt("flight_id"));

        return feedback;
    }
}
