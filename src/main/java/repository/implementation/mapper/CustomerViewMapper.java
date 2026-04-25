package repository.implementation.mapper;

import model.CustomerDetailView;
import model.CustomerOverview;
import model.enums.CustomerStatus;
import model.enums.CustomerType;
import model.enums.Packages;
import model.enums.PaymentMethod;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerViewMapper {

    public CustomerOverview mapOverview(ResultSet rs) throws SQLException {
        CustomerOverview overview = new CustomerOverview();
        overview.setCustomerId(rs.getInt("id"));
        overview.setCustomerFirstName(rs.getString("first_name"));
        overview.setCustomerLastName(rs.getString("last_name"));
        overview.setCvScore(rs.getFloat("cv_score"));
        overview.setReturning(rs.getBoolean("is_returning"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            overview.setStatus(CustomerStatus.valueOf(statusStr));
        }

        String customerTypeStr = rs.getString("customer_type");
        if (customerTypeStr != null) {
            overview.setCustomerType(CustomerType.valueOf(customerTypeStr));
        }

        String bookingPackageStr = rs.getString("booking_package");
        if (bookingPackageStr != null) {
            overview.setBookingPackage(Packages.valueOf(bookingPackageStr));
        }

        return overview;
    }

    public CustomerDetailView mapDetail(ResultSet rs) throws SQLException {
        CustomerDetailView detail = new CustomerDetailView();
        detail.setCustomerId(rs.getInt("id"));
        detail.setCustomerFirstName(rs.getString("first_name"));
        detail.setCustomerLastName(rs.getString("last_name"));
        detail.setEmail(rs.getString("email"));
        detail.setReturning(rs.getBoolean("is_returning"));
        detail.setCvScore(rs.getFloat("cv_score"));
        detail.setPreferences(rs.getString("preferences"));
        detail.setApplyToNextBooking(rs.getString("apply_to_next_booking"));
        detail.setPublicPerson(rs.getBoolean("public_person"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            detail.setStatus(CustomerStatus.valueOf(statusStr));
        }

        String customerTypeStr = rs.getString("customer_type");
        if (customerTypeStr != null) {
            detail.setCustomerType(CustomerType.valueOf(customerTypeStr));
        }

        String paymentMethodStr = rs.getString("payment_method");
        if (paymentMethodStr != null) {
            detail.setPaymentMethod(PaymentMethod.valueOf(paymentMethodStr));
        }

        return detail;
    }
}
