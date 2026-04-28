package repository.implementation;

import database.connection.ConnectionProvider;
import database.connection.DatabaseConnectionProvider;
import model.Customer;
import repository.interfaces.CustomerLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.implementation.mapper.CustomerResultSetMapper;
import repository.implementation.support.GeneratedKeyExtractor;
import repository.interfaces.CustomerSearch;
import repository.interfaces.CustomerUpdate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCustomerRepository implements CustomerLookup, CustomerUpdate, CustomerSearch {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseCustomerRepository.class);
    private final ConnectionProvider connectionProvider;
    private final CustomerResultSetMapper customerMapper;
    private final GeneratedKeyExtractor generatedKeyExtractor;

    public DatabaseCustomerRepository() {
        this(new DatabaseConnectionProvider(), new CustomerResultSetMapper(), new GeneratedKeyExtractor());
    }

    public DatabaseCustomerRepository(ConnectionProvider connectionProvider, CustomerResultSetMapper customerMapper,
                                      GeneratedKeyExtractor generatedKeyExtractor) {
        this.connectionProvider = connectionProvider;
        this.customerMapper = customerMapper;
        this.generatedKeyExtractor = generatedKeyExtractor;
    }

    @Override
    public void save(Customer customer) {
        String sql = """
            INSERT INTO customers (first_name, last_name, email, birth_date, status,
                                 is_returning, assigned_advisor_id, cv_score, preferences, apply_to_next_booking)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getBirthDate());
            pstmt.setString(5, customer.getStatus() != null ? customer.getStatus().name() : null);
            pstmt.setBoolean(6, customer.isReturning());
            if (customer.getAssignedAdvisorId() != null) {
                pstmt.setInt(7, customer.getAssignedAdvisorId());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            pstmt.setFloat(8, customer.getCvScore());
            pstmt.setString(9, customer.getPreferences());
            pstmt.setString(10, customer.getApplyToNextBooking());

            pstmt.executeUpdate();
            customer.setId(generatedKeyExtractor.extractGeneratedId(pstmt, "customer"));
            logger.info("Customer saved: {} {}", customer.getFirstName(), customer.getLastName());

        } catch (SQLException e) {
            logger.error("Error while saving customer ", e);
        }
    }

    @Override
    public void update(Customer customer) {
        String sql = """
            UPDATE customers
            SET first_name = ?, last_name = ?, email = ?, birth_date = ?, status = ?,
                is_returning = ?, assigned_advisor_id = ?, cv_score = ?,
                preferences = ?, apply_to_next_booking = ?
            WHERE id = ?;
            """;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getBirthDate());
            pstmt.setString(5, customer.getStatus() != null ? customer.getStatus().name() : null);
            pstmt.setBoolean(6, customer.isReturning());
            if (customer.getAssignedAdvisorId() != null) {
                pstmt.setInt(7, customer.getAssignedAdvisorId());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            pstmt.setFloat(8, customer.getCvScore());
            pstmt.setString(9, customer.getPreferences());
            pstmt.setString(10, customer.getApplyToNextBooking());
            pstmt.setInt(11, customer.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while updating customer {}", customer.getId(), e);
        }
    }

    @Override
    public Customer findById(int id) {
        String sql = "SELECT * FROM customers WHERE id = ?;";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return customerMapper.map(rs);
            }
        } catch (SQLException e) {
            logger.error("Error while finding customer with id " + id, e);
        }
        return null;
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers;";

        try (Connection conn = connectionProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(customerMapper.map(rs));
            }
        } catch (SQLException e) {
            logger.error("Error while getting all customers", e);
        }
        return customers;
    }

    @Override
    public List<Customer> findByStatus(String status) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE status = ?;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(customerMapper.map(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while searching customers by status {}", status, e);
        }

        return customers;
    }

    @Override
    public List<Customer> findByAdvisor(int advisorId) {
        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT * FROM customers WHERE assigned_advisor_id = ?;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, advisorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {

                    customers.add(customerMapper.map(rs));
                }
            }

            logger.info("Customers found for advisor {}: {}", advisorId, customers.size());

        } catch (SQLException e) {
            logger.error("Error while searching for customers for Advisor ID " + advisorId, e);
        }

        return customers;
    }

}
