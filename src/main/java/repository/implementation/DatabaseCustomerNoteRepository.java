package repository.implementation;

import database.connection.ConnectionProvider;
import database.connection.DatabaseConnectionProvider;
import model.CustomerNote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.implementation.mapper.CustomerNoteResultSetMapper;
import repository.implementation.support.CustomerNoteLoader;
import repository.implementation.support.GeneratedKeyExtractor;
import repository.interfaces.CustomerNoteLookup;
import repository.interfaces.CustomerNoteUpdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCustomerNoteRepository implements CustomerNoteLookup, CustomerNoteUpdate {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseCustomerNoteRepository.class);
    private final ConnectionProvider connectionProvider;
    private final CustomerNoteResultSetMapper customerNoteMapper;
    private final CustomerNoteLoader customerNoteLoader;
    private final GeneratedKeyExtractor generatedKeyExtractor;

    public DatabaseCustomerNoteRepository() {
        this(
                new DatabaseConnectionProvider(),
                new CustomerNoteResultSetMapper(),
                new CustomerNoteLoader(),
                new GeneratedKeyExtractor()
        );
    }

    public DatabaseCustomerNoteRepository(ConnectionProvider connectionProvider,
                                          CustomerNoteResultSetMapper customerNoteMapper,
                                          CustomerNoteLoader customerNoteLoader,
                                          GeneratedKeyExtractor generatedKeyExtractor) {
        this.connectionProvider = connectionProvider;
        this.customerNoteMapper = customerNoteMapper;
        this.customerNoteLoader = customerNoteLoader;
        this.generatedKeyExtractor = generatedKeyExtractor;
    }

    @Override
    public void save(CustomerNote customerNote) {
        String sql = """
            INSERT INTO customer_notes (customer_id, advisor_id, note_text, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?);
            """;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, customerNote.getCustomerId());
            pstmt.setInt(2, customerNote.getAdvisorId());
            pstmt.setString(3, customerNote.getNoteText());
            pstmt.setTimestamp(4, Timestamp.valueOf(customerNote.getCreatedAt()));
            pstmt.setTimestamp(5, Timestamp.valueOf(customerNote.getUpdatedAt()));
            pstmt.executeUpdate();

            customerNote.setId(generatedKeyExtractor.extractGeneratedId(pstmt, "customer note"));
        } catch (SQLException e) {
            logger.error("Error while saving customer note for customer {}", customerNote.getCustomerId(), e);
        }
    }

    @Override
    public void update(CustomerNote customerNote) {
        String sql = """
            UPDATE customer_notes
            SET advisor_id = ?, note_text = ?, updated_at = ?
            WHERE id = ?;
            """;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerNote.getAdvisorId());
            pstmt.setString(2, customerNote.getNoteText());
            pstmt.setTimestamp(3, Timestamp.valueOf(customerNote.getUpdatedAt()));
            pstmt.setInt(4, customerNote.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while updating customer note {}", customerNote.getId(), e);
        }
    }

    @Override
    public CustomerNote findById(int id) {
        String sql = "SELECT * FROM customer_notes WHERE id = ?;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return customerNoteMapper.map(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while finding customer note {}", id, e);
        }

        return null;
    }

    @Override
    public List<CustomerNote> findByCustomerId(int customerId) {
        try (Connection conn = connectionProvider.getConnection()) {
            return customerNoteLoader.loadByCustomerId(conn, customerId);
        } catch (SQLException e) {
            logger.error("Error while loading notes for customer {}", customerId, e);
            return new ArrayList<>();
        }
    }
}
