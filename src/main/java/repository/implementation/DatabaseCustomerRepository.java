package repository.implementation;

import database.connection.DatabaseManager;
import model.Customer;
import model.enums.CustomerStatus;
import model.enums.CustomerType;
import model.enums.PaymentMethod;
import repository.interfaces.CustomerLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.interfaces.CustomerSearch;
import repository.interfaces.CustomerUpdate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCustomerRepository implements CustomerLookup, CustomerUpdate, CustomerSearch {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseCustomerRepository.class);

    @Override
    public void save(Customer customer) {
        String sql = """
            INSERT INTO customers (first_name, last_name, email, birth_date, status, 
                                 booking_date, is_returning, assigned_advisor_id, clv_score, notes,
                                 preferences, apply_to_next_booking, marketing_purpose, newsletter_subscription,
                                 referral_code, payment_method, public_person, customer_type)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getBirthDate());
            pstmt.setString(5, customer.getStatus() != null ? customer.getStatus().name() : null);
            pstmt.setString(6, customer.getBookingDate());
            pstmt.setBoolean(7, customer.isReturning());
            if (customer.getAssignedAdvisorId() != null) {
                pstmt.setInt(8, customer.getAssignedAdvisorId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            pstmt.setFloat(9, customer.getClvScore());
            pstmt.setString(10, customer.getNotes());
            pstmt.setString(11, customer.getPreferences());
            pstmt.setString(12, customer.getApplyToNextBooking());
            pstmt.setBoolean(13, customer.isMarketingPurpose());
            pstmt.setBoolean(14, customer.isNewsletterSubscription());
            pstmt.setBoolean(15, customer.isReferralCode());
            pstmt.setString(16, customer.getPaymentMethod() != null ? customer.getPaymentMethod().name() : null);
            pstmt.setBoolean(17, customer.isPublicPerson());
            pstmt.setString(18, customer.getCustomerType() != null ? customer.getCustomerType().name() : null);

            pstmt.executeUpdate();
            customer.setId(extractGeneratedId(pstmt, "customer"));
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
                booking_date = ?, is_returning = ?, assigned_advisor_id = ?, clv_score = ?, notes = ?,
                preferences = ?, apply_to_next_booking = ?, marketing_purpose = ?, newsletter_subscription = ?,
                referral_code = ?, payment_method = ?, public_person = ?, customer_type = ?
            WHERE id = ?;
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getBirthDate());
            pstmt.setString(5, customer.getStatus() != null ? customer.getStatus().name() : null);
            pstmt.setString(6, customer.getBookingDate());
            pstmt.setBoolean(7, customer.isReturning());
            if (customer.getAssignedAdvisorId() != null) {
                pstmt.setInt(8, customer.getAssignedAdvisorId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            pstmt.setFloat(9, customer.getClvScore());
            pstmt.setString(10, customer.getNotes());
            pstmt.setString(11, customer.getPreferences());
            pstmt.setString(12, customer.getApplyToNextBooking());
            pstmt.setBoolean(13, customer.isMarketingPurpose());
            pstmt.setBoolean(14, customer.isNewsletterSubscription());
            pstmt.setBoolean(15, customer.isReferralCode());
            pstmt.setString(16, customer.getPaymentMethod() != null ? customer.getPaymentMethod().name() : null);
            pstmt.setBoolean(17, customer.isPublicPerson());
            pstmt.setString(18, customer.getCustomerType() != null ? customer.getCustomerType().name() : null);
            pstmt.setInt(19, customer.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while updating customer {}", customer.getId(), e);
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
    public List<Customer> findByStatus(String status) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE status = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
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

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, advisorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {

                    customers.add(mapResultSetToCustomer(rs));
                }
            }

            logger.info("Customers found for advisor {}: {}", advisorId, customers.size());

        } catch (SQLException e) {
            logger.error("Error while searching for customers for Advisor ID " + advisorId, e);
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

        customer.setBookingDate(rs.getString("booking_date"));
        customer.setReturning(rs.getBoolean("is_returning"));
        customer.setAssignedAdvisorId(rs.getInt("assigned_advisor_id"));
        customer.setClvScore(rs.getFloat("clv_score"));
        customer.setNotes(rs.getString("notes"));
        customer.setPreferences(rs.getString("preferences"));
        customer.setApplyToNextBooking(rs.getString("apply_to_next_booking"));
        customer.setMarketingPurpose(rs.getBoolean("marketing_purpose"));
        customer.setNewsletterSubscription(rs.getBoolean("newsletter_subscription"));
        customer.setReferralCode(rs.getBoolean("referral_code"));
        String paymentMethodStr = rs.getString("payment_method");
        if (paymentMethodStr != null) customer.setPaymentMethod(PaymentMethod.valueOf(paymentMethodStr));
        customer.setPublicPerson(rs.getBoolean("public_person"));
        String customerTypeStr = rs.getString("customer_type");
        if (customerTypeStr != null) customer.setCustomerType(CustomerType.valueOf(customerTypeStr));

        return customer;
    }

    private int extractGeneratedId(PreparedStatement pstmt, String entityName) throws SQLException {
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }

        throw new SQLException("Could not retrieve generated key for " + entityName);
    }
}
