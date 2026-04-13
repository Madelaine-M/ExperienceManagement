package repository.implementation;

import database.connection.DatabaseManager;
import model.ActionItem;
import model.enums.ActionStatus;
import model.enums.PriorityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Override
    public void save(ActionItem actionItem) {
        String sql = """
            INSERT INTO action_items (incident_id, description, suggestion_1, suggestion_2, status, priority, score_impact, expected_rec, expected_rebooking)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, actionItem.getIncidentId());
            pstmt.setString(2, actionItem.getDescription());
            pstmt.setString(3, actionItem.getSugegstion1());
            pstmt.setString(4, actionItem.getSuggestion2());
            pstmt.setString(5, actionItem.getStatus() != null ? actionItem.getStatus().name() : null);
            pstmt.setInt(6, actionItem.getPriority());
            pstmt.setDouble(7, actionItem.getScoreImpact());
            pstmt.setDouble(8, actionItem.getExpectedRec());
            pstmt.setDouble(9, actionItem.getExpectedRebooking());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    actionItem.setId(generatedKeys.getInt(1));
                }
            }

            logger.info("Action item saved for incident {}", actionItem.getIncidentId());
        } catch (SQLException e) {
            logger.error("Error while saving action item", e);
        }
    }

    @Override
    public void updateStatus(int id, String status) {
        String sql = "UPDATE action_items SET status = ? WHERE id = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while updating action item status for action item {}", id, e);
        }
    }

    @Override
    public ActionItem findById(int id) {
        String sql = "SELECT * FROM action_items WHERE id = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToActionItem(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while finding action item with id " + id, e);
        }

        return null;
    }

    @Override
    public List<ActionItem> findByIncidentId(int incidentId) {
        List<ActionItem> actionItems = new ArrayList<>();
        String sql = "SELECT * FROM action_items WHERE incident_id = ? ORDER BY priority DESC, id ASC;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, incidentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    actionItems.add(mapResultSetToActionItem(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading action items for incident " + incidentId, e);
        }

        return actionItems;
    }
    
    private ActionItem mapResultSetToActionItem(ResultSet rs) throws SQLException {
        ActionItem actionItem = new ActionItem();
        actionItem.setId(rs.getInt("id"));
        actionItem.setIncidentId(rs.getInt("incident_id"));
        actionItem.setDescription(rs.getString("description"));
        actionItem.setSugegstion1(rs.getString("suggestion_1"));
        actionItem.setSuggestion2(rs.getString("suggestion_2"));

        String status = rs.getString("status");
        if (status != null) {
            actionItem.setStatus(ActionStatus.valueOf(status));
        }

        actionItem.setPriority(rs.getInt("priority"));
        actionItem.setScoreImpact(rs.getDouble("score_impact"));
        actionItem.setExpectedRec(rs.getDouble("expected_rec"));
        actionItem.setExpectedRebooking(rs.getDouble("expected_rebooking"));
        return actionItem;
    }

    private int minimumPriorityFor(PriorityLevel priorityLevel) {
        if (priorityLevel == null) {
            return 0;
        }

        return switch (priorityLevel) {
            case LOW -> 1;
            case MEDIUM -> 4;
            case HIGH -> 7;
            case CRITICAL -> 9;
        };
    }

    @Override
    public List<ActionItem> findPriorityActionsForAdvisor(int advisorId) {
        List<ActionItem> actions = new ArrayList<>();

        String sql = """
        SELECT ai.*
        FROM action_items ai
        JOIN incidents i ON ai.incident_id = i.id
        WHERE i.assigned_advisor_id = ? 
        AND ai.status = 'SUGGESTED'
        ORDER BY ai.priority DESC;
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, advisorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    actions.add(mapResultSetToActionItem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }

        return actions;
    }
}
