package simulation.persistence;

import database.connection.ConnectionProvider;
import database.connection.DatabaseConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseSimulationDataCleanupService implements SimulationDataCleanupService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSimulationDataCleanupService.class);
    private static final String SIMULATION_EMAIL_PATTERN = "sim.customer.%@example.com";

    private final ConnectionProvider connectionProvider;

    public DatabaseSimulationDataCleanupService() {
        this(new DatabaseConnectionProvider());
    }

    public DatabaseSimulationDataCleanupService(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void cleanupGeneratedData() {
        String deleteCustomersSql = "DELETE FROM customers WHERE email LIKE ?;";

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteCustomersSql)) {

            pstmt.setString(1, SIMULATION_EMAIL_PATTERN);
            int deletedCustomers = pstmt.executeUpdate();
            logger.info("Deleted {} simulation-generated customer(s) during app shutdown.", deletedCustomers);

        } catch (SQLException e) {
            logger.error("Error while cleaning up simulation-generated data.", e);
        }
    }
}
