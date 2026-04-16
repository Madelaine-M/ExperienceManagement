package repository.implementation;

import database.connection.DatabaseManager;
import model.Advisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.interfaces.AdvisorRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAdvisorRepository implements AdvisorRepository {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseAdvisorRepository.class);

    @Override
    public void save(Advisor advisor) {
        String sql = """
            INSERT INTO advisors (first_name, last_name, email, speciality, workload_score)
            VALUES (?, ?, ?, ?, ?);
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, advisor.getFirstName());
            pstmt.setString(2, advisor.getLastName());
            pstmt.setString(3, advisor.getEmail());
            pstmt.setString(4, advisor.getSpeciality());
            pstmt.setDouble(5, advisor.getWorkloadScore());

            pstmt.executeUpdate();
            advisor.setId(extractGeneratedId(pstmt, "advisor"));
            logger.info("Advisor saved: {} {}", advisor.getFirstName(), advisor.getLastName());

        } catch (SQLException e) {
            logger.error("Error while saving advisor", e);
        }
    }

    @Override
    public Advisor findById(int id) {
        String sql = "SELECT * FROM advisors WHERE id = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdvisor(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while finding advisor with id " + id, e);
        }

        return null;
    }

    @Override
    public List<Advisor> findAll() {
        List<Advisor> advisors = new ArrayList<>();
        String sql = "SELECT * FROM advisors;";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                advisors.add(mapResultSetToAdvisor(rs));
            }
        } catch (SQLException e) {
            logger.error("Error while getting all advisors", e);
        }

        return advisors;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM advisors WHERE id = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Advisor with id {} was deleted", id);
            }
        } catch (SQLException e) {
            logger.error("Error while deleting advisor", e);
        }
    }

    @Override
    public List<Advisor> findBySpeciality(String speciality) {
        List<Advisor> advisors = new ArrayList<>();
        String sql = "SELECT * FROM advisors WHERE speciality = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, speciality);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    advisors.add(mapResultSetToAdvisor(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while searching advisors by speciality " + speciality, e);
        }

        return advisors;
    }

    private Advisor mapResultSetToAdvisor(ResultSet rs) throws SQLException {
        Advisor advisor = new Advisor();
        advisor.setId(rs.getInt("id"));
        advisor.setFirstName(rs.getString("first_name"));
        advisor.setLastName(rs.getString("last_name"));
        advisor.setEmail(rs.getString("email"));
        advisor.setSpeciality(rs.getString("speciality"));
        advisor.setWorkloadScore(rs.getDouble("workload_score"));
        return advisor;
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
