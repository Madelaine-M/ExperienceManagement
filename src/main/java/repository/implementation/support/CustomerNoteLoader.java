package repository.implementation.support;

import model.domain.CustomerNote;
import repository.implementation.mapper.CustomerNoteResultSetMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerNoteLoader {
    private final CustomerNoteResultSetMapper customerNoteMapper;

    public CustomerNoteLoader() {
        this(new CustomerNoteResultSetMapper());
    }

    public CustomerNoteLoader(CustomerNoteResultSetMapper customerNoteMapper) {
        this.customerNoteMapper = customerNoteMapper;
    }

    public List<CustomerNote> loadByCustomerId(Connection conn, int customerId) throws SQLException {
        List<CustomerNote> notes = new ArrayList<>();
        String sql = """
            SELECT *
            FROM customer_notes
            WHERE customer_id = ?
            ORDER BY updated_at DESC, created_at DESC, id DESC;
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notes.add(customerNoteMapper.map(rs));
                }
            }
        }

        return notes;
    }
}
