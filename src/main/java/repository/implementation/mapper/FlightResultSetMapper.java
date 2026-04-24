package repository.implementation.mapper;

import model.Flight;
import model.enums.Packages;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FlightResultSetMapper {

    public Flight map(ResultSet rs) throws SQLException {
        Flight flight = new Flight();
        flight.setId(rs.getInt("id"));
        flight.setCustomerId(rs.getInt("customer_id"));
        flight.setFlightNumber(rs.getString("flight_number"));
        flight.setBookingDate(rs.getString("booking_date"));
        flight.setFlightDate(rs.getString("flight_date"));
        flight.setStatus(rs.getString("status"));
        flight.setCurrent(rs.getBoolean("is_current"));

        String bookingPackage = rs.getString("booking_package");
        if (bookingPackage != null) {
            flight.setBookingPackage(Packages.valueOf(bookingPackage));
        }

        return flight;
    }
}
