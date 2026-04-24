package repository.implementation.mapper;

import model.Customer;
import model.enums.CustomerStatus;
import model.enums.CustomerType;
import model.enums.PaymentMethod;

import java.sql.ResultSet;
import java.sql.SQLException;

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
        customer.setCvScore(rs.getFloat("cv_score"));
        customer.setPreferences(rs.getString("preferences"));
        customer.setApplyToNextBooking(rs.getString("apply_to_next_booking"));
        customer.setMarketingPurpose(rs.getBoolean("marketing_purpose"));
        customer.setNewsletterSubscription(rs.getBoolean("newsletter_subscription"));
        customer.setReferralCode(rs.getBoolean("referral_code"));

        String paymentMethodStr = rs.getString("payment_method");
        if (paymentMethodStr != null) {
            customer.setPaymentMethod(PaymentMethod.valueOf(paymentMethodStr));
        }

        customer.setPublicPerson(rs.getBoolean("public_person"));

        String customerTypeStr = rs.getString("customer_type");
        if (customerTypeStr != null) {
            customer.setCustomerType(CustomerType.valueOf(customerTypeStr));
        }

        return customer;
    }
}
