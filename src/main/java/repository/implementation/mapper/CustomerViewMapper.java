package repository.implementation.mapper;

import model.view.CustomerDetailView;
import model.view.CustomerOverview;
import model.enums.CustomerStatus;
import model.enums.Packages;

import java.sql.ResultSet;
import java.sql.SQLException;

//AI was used to implement the idea of mapping between database to object
public class CustomerViewMapper {

    public CustomerOverview mapOverview(ResultSet rs) throws SQLException {
        CustomerOverview overview = new CustomerOverview();
        overview.setCustomerId(rs.getInt("id"));
        overview.setCustomerFirstName(rs.getString("first_name"));
        overview.setCustomerLastName(rs.getString("last_name"));
        overview.setReturning(rs.getBoolean("is_returning"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            overview.setStatus(CustomerStatus.valueOf(statusStr));
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
        detail.setPreferences(rs.getString("preferences"));
        detail.setApplyToNextBooking(rs.getString("apply_to_next_booking"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            detail.setStatus(CustomerStatus.valueOf(statusStr));
        }

        return detail;
    }
}
