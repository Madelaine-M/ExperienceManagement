package repository.implementation;

import database.connection.DatabaseManager;
import model.Customer;
import model.enums.CustomerStatus;
import model.enums.Packages;
import repository.interfaces.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCustomerRepository implements CustomerRepository {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseCustomerRepository.class);

    @Override
    public void save(Customer customer) {
        String sql = """
            INSERT INTO customers (first_name, last_name, email, birth_date, status, 
                                 booking_package, booking_date, flight_date,
                                 is_returning, assigned_advisor_id, clv_score)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getBirthDate());
            pstmt.setString(5, customer.getStatus() != null ? customer.getStatus().name() : null);
            pstmt.setString(6, customer.getBookingPackage() != null ? customer.getBookingPackage().name() : null);
            pstmt.setString(7, customer.getBookingDate());
            pstmt.setString(8, customer.getFlightDate());
            pstmt.setBoolean(9, customer.isReturning());
            if (customer.getAssignedAdvisorId() > 0) {
                pstmt.setInt(10, customer.getAssignedAdvisorId());
            } else {
                pstmt.setNull(10, Types.INTEGER);
            }
            pstmt.setFloat(11, customer.getClvScore());

            pstmt.executeUpdate();
            logger.info("Customer saved: {} {}", customer.getFirstName(), customer.getLastName());

        } catch (SQLException e) {
            logger.error("Error while saving customer ", e);
        }
    }

    @Override
    public Customer findById(int id) {
        String sql = "SELECT * FROM customers WHERE id = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
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

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            logger.error("Error while getting all customers", e);
        }
        return customers;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM customers WHERE id = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Customer with id {} was deleted", id);
            }
        } catch (SQLException e) {
            logger.error("Error while deleting customer", e);
        }
    }

    @Override
    public List<Customer> findByAdvisor(int advisorId) {
        List<Customer> customers = new ArrayList<>();
        // SQL: Suche alle Kunden, bei denen die Berater-ID passt
        String sql = "SELECT * FROM customers WHERE assigned_advisor_id = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, advisorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Hier nutzen wir wieder unsere praktische Hilfsmethode
                    customers.add(mapResultSetToCustomer(rs));
                }
            }

            logger.info("Customers found for advisor {}: {}", advisorId, customers.size());

        } catch (SQLException e) {
            logger.error("Error while searching for customers for Advisor ID " + advisorId, e);
        }

        return customers;
    }

    @Override
    public List<Customer> findAtRisk(double clvThreshold) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE clv_score < ? ORDER BY clv_score ASC;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, clvThreshold);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while searching for at-risk customers below CLV {}", clvThreshold, e);
        }

        return customers;
    }

    // Hilfsmethode: Wandelt eine DB-Zeile (ResultSet) in ein Java-Objekt (Customer) um
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setEmail(rs.getString("email"));
        customer.setBirthDate(rs.getString("birth_date"));

        // Enums zurückwandeln (Strings aus DB -> Java Enums)
        String statusStr = rs.getString("status");
        if (statusStr != null) customer.setStatus(CustomerStatus.valueOf(statusStr));

        String packageStr = rs.getString("booking_package");
        if (packageStr != null) customer.setBookingPackage(Packages.valueOf(packageStr));

        customer.setBookingDate(rs.getString("booking_date"));
        customer.setFlightDate(rs.getString("flight_date"));
        customer.setReturning(rs.getBoolean("is_returning"));
        customer.setAssignedAdvisorId(rs.getInt("assigned_advisor_id"));
        customer.setClvScore(rs.getFloat("clv_score"));

        return customer;
    }
}
