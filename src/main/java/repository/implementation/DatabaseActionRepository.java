package repository.implementation;

import database.connection.ConnectionProvider;
import database.connection.DatabaseConnectionProvider;
import model.domain.ActionItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.RepositoryException;
import repository.implementation.mapper.ActionItemResultSetMapper;
import repository.implementation.support.GeneratedKeyExtractor;
import repository.interfaces.ActionLookup;
import repository.interfaces.ActionManagement;
import repository.interfaces.ActionUpdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseActionRepository implements ActionLookup, ActionUpdate, ActionManagement {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseActionRepository.class);
    private final ConnectionProvider connectionProvider;
    private final ActionItemResultSetMapper actionItemMapper;
    private final GeneratedKeyExtractor generatedKeyExtractor;

    public DatabaseActionRepository() {
        this(new DatabaseConnectionProvider(), new ActionItemResultSetMapper(), new GeneratedKeyExtractor());
    }

    public DatabaseActionRepository(ConnectionProvider connectionProvider, ActionItemResultSetMapper actionItemMapper,
                                    GeneratedKeyExtractor generatedKeyExtractor) {
        this.connectionProvider = connectionProvider;
        this.actionItemMapper = actionItemMapper;
        this.generatedKeyExtractor = generatedKeyExtractor;
    }

    @Override
    public void save(ActionItem actionItem) {
        String sql = """
            INSERT INTO action_items (incident_id, description, suggestion_1, suggestion_2, status, score_impact, expected_rec, expected_rebooking)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?);
            """;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, actionItem.getIncidentId());
            pstmt.setString(2, actionItem.getDescription());
            pstmt.setString(3, actionItem.getSuggestion1());
            pstmt.setString(4, actionItem.getSuggestion2());
            pstmt.setString(5, actionItem.getStatus() != null ? actionItem.getStatus().name() : null);
            pstmt.setDouble(6, actionItem.getScoreImpact());
            pstmt.setDouble(7, actionItem.getExpectedRec());
            pstmt.setDouble(8, actionItem.getExpectedRebooking());
            pstmt.executeUpdate();

            actionItem.setId(generatedKeyExtractor.extractGeneratedId(pstmt, "action item"));

            logger.info("Action item saved for incident {}", actionItem.getIncidentId());
        } catch (SQLException e) {
            logger.error("Error while saving action item", e);
            throw new RepositoryException("Failed to save action item for incident " + actionItem.getIncidentId(), e);
        }
    }

    @Override
    public void updateStatus(int id, String status) {
        String sql = "UPDATE action_items SET status = ? WHERE id = ?;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while updating action item status for action item {}", id, e);
            throw new RepositoryException("Failed to update action item status for action item " + id, e);
        }
    }

    @Override
    public ActionItem findById(int id) {
        String sql = "SELECT * FROM action_items WHERE id = ?;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return actionItemMapper.map(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while finding action item with id " + id, e);
            throw new RepositoryException("Failed to find action item " + id, e);
        }

        return null;
    }

    @Override
    public List<ActionItem> findByIncidentId(int incidentId) {
        List<ActionItem> actionItems = new ArrayList<>();
        String sql = "SELECT * FROM action_items WHERE incident_id = ? ORDER BY id ASC;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, incidentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    actionItems.add(actionItemMapper.map(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading action items for incident " + incidentId, e);
            throw new RepositoryException("Failed to load action items for incident " + incidentId, e);
        }

        return actionItems;
    }
    
    @Override
    public List<ActionItem> findSuggestedActionsByAdvisorId(int advisorId) {
        List<ActionItem> actions = new ArrayList<>();

        String sql = """
        SELECT ai.*
        FROM action_items ai
        JOIN incidents i ON ai.incident_id = i.id
        WHERE i.assigned_advisor_id = ? 
        AND ai.status = 'SUGGESTED'
        ORDER BY ai.id ASC;
        """;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, advisorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    actions.add(actionItemMapper.map(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading priority actions for advisor {}", advisorId, e);
            throw new RepositoryException("Failed to load suggested actions for advisor " + advisorId, e);
        }

        return actions;
    }
}
