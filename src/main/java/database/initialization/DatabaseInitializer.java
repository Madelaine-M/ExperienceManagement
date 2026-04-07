package database.initialization;

import database.connection.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    public static void initialize() {
        String createCustomers = """
            CREATE TABLE IF NOT EXISTS customers (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                email TEXT UNIQUE,
                birth_date TEXT,
                status TEXT,
                booking_package TEXT,
                booking_date TEXT,
                flight_date TEXT,
                is_returning INTEGER DEFAULT 0,
                assigned_advisor_id INTEGER,
                clv_score REAL DEFAULT 0.0,

                FOREIGN KEY (assigned_advisor_id) REFERENCES advisors(id)
                    ON DELETE SET NULL
            );
            """;
        String createIncidents = """
            CREATE TABLE IF NOT EXISTS incidents (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER,
                type TEXT,
                description TEXT,
                priority_score REAL DEFAULT 0.0,
                score_impact REAL DEFAULT 0.0,
                status TEXT,
                assigned_advisor_id INTEGER,
                source_feedback_item_id INTEGER,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                FOREIGN KEY (customer_id) REFERENCES customers(id)
                    ON DELETE CASCADE,
                FOREIGN KEY (assigned_advisor_id) REFERENCES advisors(id)
                    ON DELETE SET NULL,
                FOREIGN KEY (source_feedback_item_id) REFERENCES feedback_items(id)
                    ON DELETE SET NULL
            );
            """;
        String createFeedbacks = """
            CREATE TABLE IF NOT EXISTS feedbacks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                FOREIGN KEY (customer_id) REFERENCES customers(id)
                    ON DELETE CASCADE
            );
            """;
        String createFeedbackItems = """
            CREATE TABLE IF NOT EXISTS feedback_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                feedback_id INTEGER NOT NULL,
                category TEXT NOT NULL,
                score INTEGER NOT NULL,
                comment TEXT,

                FOREIGN KEY (feedback_id) REFERENCES feedbacks(id)
                    ON DELETE CASCADE
            );
            """;
        String createAdvisors = """
            CREATE TABLE IF NOT EXISTS advisors (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                email TEXT UNIQUE,
                speciality TEXT,
                workload_score REAL DEFAULT 0.0
            );
            """;
        String createActionItems = """
            CREATE TABLE IF NOT EXISTS action_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                incident_id INTEGER NOT NULL,
                description TEXT NOT NULL,
                status TEXT NOT NULL,
                priority INTEGER NOT NULL,

                FOREIGN KEY (incident_id) REFERENCES incidents(id)
                    ON DELETE CASCADE
            );
            """;
        String createSentimentHistory = """
            CREATE TABLE IF NOT EXISTS sentiment_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER NOT NULL,
                recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                clv_score REAL,
                score_impact REAL,

                FOREIGN KEY (customer_id) REFERENCES customers(id)
                    ON DELETE CASCADE
            );
            """;

        try (Connection conn = DatabaseManager.getConnection()) {

            executeSql(conn, createAdvisors);
            executeSql(conn, createCustomers);
            executeSql(conn, createFeedbacks);
            executeSql(conn, createFeedbackItems);
            executeSql(conn, createIncidents);
            executeSql(conn, createActionItems);
            executeSql(conn, createSentimentHistory);
            ensureCustomerColumnsExist(conn);
            ensureIncidentColumnsExist(conn);

            logger.info("Database was correctly initialized");

        } catch (Exception e) {
            logger.error("Initialization error", e);
        }
    }

    private static void ensureIncidentColumnsExist(Connection conn) throws SQLException {
        if (!columnExists(conn, "incidents", "assigned_advisor_id")) {
            executeSql(conn, """
                ALTER TABLE incidents
                ADD COLUMN assigned_advisor_id INTEGER REFERENCES advisors(id) ON DELETE SET NULL;
                """);
        }

        if (!columnExists(conn, "incidents", "source_feedback_item_id")) {
            executeSql(conn, """
                ALTER TABLE incidents
                ADD COLUMN source_feedback_item_id INTEGER REFERENCES feedback_items(id) ON DELETE SET NULL;
                """);
        }
    }

    private static void ensureCustomerColumnsExist(Connection conn) throws SQLException {
        if (!columnExists(conn, "customers", "is_returning")) {
            executeSql(conn, """
                ALTER TABLE customers
                ADD COLUMN is_returning INTEGER DEFAULT 0;
                """);
        }
    }

    private static void executeSql(Connection conn, String sql) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        }
    }

    private static boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("PRAGMA table_info(" + tableName + ");");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
        }

        return false;
    }
}
