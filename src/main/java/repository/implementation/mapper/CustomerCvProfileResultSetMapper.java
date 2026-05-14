package repository.implementation.mapper;

import model.domain.CustomerCvProfile;
import model.enums.CustomerType;
import model.enums.PaymentMethod;

import java.sql.ResultSet;
import java.sql.SQLException;

//was implemented based on AI implemented mapper CustomerResultSetMapper
public class CustomerCvProfileResultSetMapper {

    public CustomerCvProfile map(ResultSet rs) throws SQLException {
        CustomerCvProfile profile = new CustomerCvProfile();
        profile.setCustomerId(rs.getInt("customer_id"));
        profile.setMarketingPurpose(rs.getBoolean("marketing_purpose"));
        profile.setNewsletterSubscription(rs.getBoolean("newsletter_subscription"));
        profile.setReferralCode(rs.getBoolean("referral_code"));
        profile.setPublicPerson(rs.getBoolean("public_person"));

        String paymentMethodStr = rs.getString("payment_method");
        if (paymentMethodStr != null) {
            profile.setPaymentMethod(PaymentMethod.valueOf(paymentMethodStr));
        }

        String customerTypeStr = rs.getString("customer_type");
        if (customerTypeStr != null) {
            profile.setCustomerType(CustomerType.valueOf(customerTypeStr));
        }

        return profile;
    }
}
