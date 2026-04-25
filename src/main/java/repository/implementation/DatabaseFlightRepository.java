package repository.implementation;

import database.connection.ConnectionProvider;
import database.connection.DatabaseConnectionProvider;
import model.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.implementation.mapper.FlightResultSetMapper;
import repository.implementation.support.GeneratedKeyExtractor;
import repository.interfaces.FlightRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFlightRepository implements FlightRepository {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseFlightRepository.class);
    private final ConnectionProvider connectionProvider;
    private final FlightResultSetMapper flightMapper;
    private final GeneratedKeyExtractor generatedKeyExtractor;

    public DatabaseFlightRepository() {
        this(new DatabaseConnectionProvider(), new FlightResultSetMapper(), new GeneratedKeyExtractor());
    }

    public DatabaseFlightRepository(ConnectionProvider connectionProvider, FlightResultSetMapper flightMapper,
                                    GeneratedKeyExtractor generatedKeyExtractor) {
        this.connectionProvider = connectionProvider;
        this.flightMapper = flightMapper;
        this.generatedKeyExtractor = generatedKeyExtractor;
    }

    @Override
    public void save(Flight flight) {
        String sql = """
            INSERT INTO flights (customer_id, flight_number, booking_date, flight_date, booking_package, status, is_current)
            VALUES (?, ?, ?, ?, ?, ?, ?);
            """;

        String clearCurrentSql = "UPDATE flights SET is_current = 0 WHERE customer_id = ?;";

        try (Connection conn = connectionProvider.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement clearCurrentStmt = conn.prepareStatement(clearCurrentSql);
                 PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                if (flight.isCurrent()) {
                    clearCurrentStmt.setInt(1, flight.getCustomerId());
                    clearCurrentStmt.executeUpdate();
                }

                pstmt.setInt(1, flight.getCustomerId());
                pstmt.setString(2, flight.getFlightNumber());
                pstmt.setString(3, flight.getBookingDate());
                pstmt.setString(4, flight.getFlightDate());
                pstmt.setString(5, flight.getBookingPackage() != null ? flight.getBookingPackage().name() : null);
                pstmt.setString(6, flight.getStatus());
                pstmt.setBoolean(7, flight.isCurrent());
                pstmt.executeUpdate();

                flight.setId(generatedKeyExtractor.extractGeneratedId(pstmt, "flight"));

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.error("Error while saving flight", e);
        }
    }

    @Override
    public Flight findById(int id) {
        String sql = "SELECT * FROM flights WHERE id = ?;";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return flightMapper.map(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while finding flight {}", id, e);
        }
        return null;
    }

    @Override
    public Flight findCurrentByCustomerId(int customerId) {
        String sql = """
            SELECT *
            FROM flights
            WHERE customer_id = ? AND is_current = 1
            ORDER BY id DESC
            LIMIT 1;
            """;
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return flightMapper.map(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while finding current flight for customer {}", customerId, e);
        }
        return null;
    }

    @Override
    public Flight findLatestPreviousByCustomerId(int customerId) {
        String sql = """
            SELECT *
            FROM flights
            WHERE customer_id = ? AND is_current = 0
            ORDER BY flight_date DESC, id DESC
            LIMIT 1;
            """;
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return flightMapper.map(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while finding latest previous flight for customer {}", customerId, e);
        }
        return null;
    }

    @Override
    public List<Flight> findByCustomerId(int customerId) {
        List<Flight> flights = new ArrayList<>();
        String sql = """
            SELECT *
            FROM flights
            WHERE customer_id = ?
            ORDER BY is_current DESC, flight_date DESC, id DESC;
            """;
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    flights.add(flightMapper.map(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading flights for customer {}", customerId, e);
        }
        return flights;
    }

    @Override
    public List<Flight> findPreviousByCustomerId(int customerId) {
        List<Flight> flights = new ArrayList<>();
        String sql = """
            SELECT *
            FROM flights
            WHERE customer_id = ? AND is_current = 0
            ORDER BY flight_date DESC, id DESC;
            """;
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    flights.add(flightMapper.map(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading previous flights for customer {}", customerId, e);
        }
        return flights;
    }

}
