package repository.implementation.mapper;

import model.domain.CustomerNote;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

//was implemented based on AI implemented mapper CustomerResultSetMapper
public class CustomerNoteResultSetMapper {

    public CustomerNote map(ResultSet rs) throws SQLException {
        CustomerNote customerNote = new CustomerNote();
        customerNote.setId(rs.getInt("id"));
        customerNote.setCustomerId(rs.getInt("customer_id"));
        customerNote.setAdvisorId(rs.getInt("advisor_id"));
        customerNote.setNoteText(rs.getString("note_text"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            customerNote.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            customerNote.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return customerNote;
    }
}
