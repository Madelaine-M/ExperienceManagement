package repository.implementation;

import database.connection.DatabaseManager;
import model.ActionItem;
import model.enums.ActionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.interfaces.ActionRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseActionRepository implements ActionRepository {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseActionRepository.class);

    @Override
    public void save(ActionItem actionItem) {
        String sql = """
            INSERT INTO action_items (incident_id, description, status, priority)
            VALUES (?, ?, ?, ?);
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, actionItem.getIncidentId());
            pstmt.setString(2, actionItem.getDescription());
            pstmt.setString(3, actionItem.getStatus() != null ? actionItem.getStatus().name() : null);
            pstmt.setInt(4, actionItem.getPriority());
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

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM action_items WHERE id = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while deleting action item", e);
        }
    }

    private ActionItem mapResultSetToActionItem(ResultSet rs) throws SQLException {
        ActionItem actionItem = new ActionItem();
        actionItem.setId(rs.getInt("id"));
        actionItem.setIncidentId(rs.getInt("incident_id"));
        actionItem.setDescription(rs.getString("description"));

        String status = rs.getString("status");
        if (status != null) {
            actionItem.setStatus(ActionStatus.valueOf(status));
        }

        actionItem.setPriority(rs.getInt("priority"));
        return actionItem;
    }
}
