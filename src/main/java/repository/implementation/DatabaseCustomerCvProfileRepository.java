package repository.implementation;

import database.connection.ConnectionProvider;
import model.domain.CustomerCvProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.RepositoryException;
import repository.implementation.mapper.CustomerCvProfileResultSetMapper;
import repository.interfaces.CustomerCvProfileLookup;
import repository.interfaces.CustomerCvProfileUpdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseCustomerCvProfileRepository implements CustomerCvProfileLookup, CustomerCvProfileUpdate {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseCustomerCvProfileRepository.class);
    private final ConnectionProvider connectionProvider;
    private final CustomerCvProfileResultSetMapper profileMapper;

    public DatabaseCustomerCvProfileRepository(ConnectionProvider connectionProvider,
                                               CustomerCvProfileResultSetMapper profileMapper) {
        this.connectionProvider = connectionProvider;
        this.profileMapper = profileMapper;
    }

    @Override
    public CustomerCvProfile findByCustomerId(int customerId) {
        String sql = "SELECT * FROM customer_cv_profiles WHERE customer_id = ?;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return profileMapper.map(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading CV profile for customer {}", customerId, e);
            throw new RepositoryException("Failed to load CV profile for customer " + customerId, e);
        }

        CustomerCvProfile profile = new CustomerCvProfile();
        profile.setCustomerId(customerId);
        return profile;
    }

    @Override
    public void save(CustomerCvProfile profile) {
        String sql = """
            INSERT INTO customer_cv_profiles (
                customer_id, marketing_purpose, newsletter_subscription, referral_code,
                payment_method, public_person, customer_type
            )
            VALUES (?, ?, ?, ?, ?, ?, ?);
            """;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            bindProfile(pstmt, profile);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while saving CV profile for customer {}", profile.getCustomerId(), e);
            throw new RepositoryException("Failed to save CV profile for customer " + profile.getCustomerId(), e);
        }
    }

    @Override
    public void update(CustomerCvProfile profile) {
        String sql = """
            UPDATE customer_cv_profiles
            SET marketing_purpose = ?,
                newsletter_subscription = ?,
                referral_code = ?,
                payment_method = ?,
                public_person = ?,
                customer_type = ?
            WHERE customer_id = ?;
            """;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, profile.isMarketingPurpose());
            pstmt.setBoolean(2, profile.isNewsletterSubscription());
            pstmt.setBoolean(3, profile.isReferralCode());
            pstmt.setString(4, profile.getPaymentMethod() != null ? profile.getPaymentMethod().name() : null);
            pstmt.setBoolean(5, profile.isPublicPerson());
            pstmt.setString(6, profile.getCustomerType() != null ? profile.getCustomerType().name() : null);
            pstmt.setInt(7, profile.getCustomerId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while updating CV profile for customer {}", profile.getCustomerId(), e);
            throw new RepositoryException("Failed to update CV profile for customer " + profile.getCustomerId(), e);
        }
    }

    private void bindProfile(PreparedStatement pstmt, CustomerCvProfile profile) throws SQLException {
        pstmt.setInt(1, profile.getCustomerId());
        pstmt.setBoolean(2, profile.isMarketingPurpose());
        pstmt.setBoolean(3, profile.isNewsletterSubscription());
        pstmt.setBoolean(4, profile.isReferralCode());
        pstmt.setString(5, profile.getPaymentMethod() != null ? profile.getPaymentMethod().name() : null);
        pstmt.setBoolean(6, profile.isPublicPerson());
        pstmt.setString(7, profile.getCustomerType() != null ? profile.getCustomerType().name() : null);
    }
}
