package repository.implementation;

import database.connection.ConnectionProvider;
import model.domain.CustomerNote;
import model.enums.ActionStatus;
import model.enums.IncidentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.RepositoryException;
import repository.interfaces.RecommendationResolutionStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DatabaseRecommendationResolutionStore implements RecommendationResolutionStore {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseRecommendationResolutionStore.class);
    private final ConnectionProvider connectionProvider;

    public DatabaseRecommendationResolutionStore(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void completeRecommendationResolution(int incidentId, CustomerNote note) {
        String updateActionsSql = "UPDATE action_items SET status = ? WHERE incident_id = ?;";
        String updateIncidentSql = "UPDATE incidents SET status = ? WHERE id = ?;";
        String insertNoteSql = """
                INSERT INTO customer_notes (customer_id, advisor_id, note_text, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?);
                """;

        try (Connection connection = connectionProvider.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement actionStatement = connection.prepareStatement(updateActionsSql);
                 PreparedStatement incidentStatement = connection.prepareStatement(updateIncidentSql);
                 PreparedStatement noteStatement = connection.prepareStatement(insertNoteSql)) {

                actionStatement.setString(1, ActionStatus.COMPLETED.name());
                actionStatement.setInt(2, incidentId);
                int updatedActions = actionStatement.executeUpdate();
                if (updatedActions <= 0) {
                    throw new IllegalStateException("No action items were completed for incident " + incidentId + ".");
                }

                incidentStatement.setString(1, IncidentStatus.CLOSED.name());
                incidentStatement.setInt(2, incidentId);
                int updatedIncidents = incidentStatement.executeUpdate();
                if (updatedIncidents != 1) {
                    throw new IllegalStateException("Incident " + incidentId + " could not be closed.");
                }

                noteStatement.setInt(1, note.getCustomerId());
                noteStatement.setInt(2, note.getAdvisorId());
                noteStatement.setString(3, note.getNoteText());
                noteStatement.setTimestamp(4, Timestamp.valueOf(note.getCreatedAt()));
                noteStatement.setTimestamp(5, Timestamp.valueOf(note.getUpdatedAt()));
                int insertedNotes = noteStatement.executeUpdate();
                if (insertedNotes != 1) {
                    throw new IllegalStateException("Recommendation note could not be saved.");
                }

                connection.commit();
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            logger.error("Error while completing recommendation resolution for incident {}", incidentId, exception);
            throw new RepositoryException("Failed to complete recommendation resolution for incident " + incidentId, exception);
        }
    }
}
