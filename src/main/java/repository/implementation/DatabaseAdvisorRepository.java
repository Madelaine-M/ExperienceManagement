package repository.implementation;

import database.connection.ConnectionProvider;
import model.domain.Advisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.RepositoryException;
import repository.implementation.mapper.AdvisorResultSetMapper;
import repository.implementation.support.GeneratedKeyExtractor;
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
    private final ConnectionProvider connectionProvider;
    private final AdvisorResultSetMapper advisorMapper;
    private final GeneratedKeyExtractor generatedKeyExtractor;

    public DatabaseAdvisorRepository(ConnectionProvider connectionProvider, AdvisorResultSetMapper advisorMapper,
                                     GeneratedKeyExtractor generatedKeyExtractor) {
        this.connectionProvider = connectionProvider;
        this.advisorMapper = advisorMapper;
        this.generatedKeyExtractor = generatedKeyExtractor;
    }

    @Override
    public void save(Advisor advisor) {
        String sql = """
            INSERT INTO advisors (first_name, last_name, email, speciality)
            VALUES (?, ?, ?, ?, ?);
            """;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, advisor.getFirstName());
            pstmt.setString(2, advisor.getLastName());
            pstmt.setString(3, advisor.getEmail());
            pstmt.setString(4, advisor.getSpeciality());

            pstmt.executeUpdate();
            advisor.setId(generatedKeyExtractor.extractGeneratedId(pstmt, "advisor"));
            logger.info("Advisor saved: {} {}", advisor.getFirstName(), advisor.getLastName());

        } catch (SQLException e) {
            logger.error("Error while saving advisor", e);
            throw new RepositoryException("Failed to save advisor " + advisor.getEmail(), e);
        }
    }

    @Override
    public Advisor findById(int id) {
        String sql = "SELECT * FROM advisors WHERE id = ?;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return advisorMapper.map(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while finding advisor with id " + id, e);
            throw new RepositoryException("Failed to find advisor " + id, e);
        }

        return null;
    }

    @Override
    public List<Advisor> findAll() {
        List<Advisor> advisors = new ArrayList<>();
        String sql = "SELECT * FROM advisors;";

        try (Connection conn = connectionProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                advisors.add(advisorMapper.map(rs));
            }
        } catch (SQLException e) {
            logger.error("Error while getting all advisors", e);
            throw new RepositoryException("Failed to load advisors", e);
        }

        return advisors;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM advisors WHERE id = ?;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Advisor with id {} was deleted", id);
            }
        } catch (SQLException e) {
            logger.error("Error while deleting advisor", e);
            throw new RepositoryException("Failed to delete advisor " + id, e);
        }
    }

    @Override
    public List<Advisor> findBySpeciality(String speciality) {
        List<Advisor> advisors = new ArrayList<>();
        String sql = "SELECT * FROM advisors WHERE speciality = ?;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, speciality);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    advisors.add(advisorMapper.map(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while searching advisors by speciality " + speciality, e);
            throw new RepositoryException("Failed to find advisors by speciality " + speciality, e);
        }

        return advisors;
    }

}
