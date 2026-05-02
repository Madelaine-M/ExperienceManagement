package database.initialization;

import database.connection.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    private DatabaseInitializer() {
    }

    public static void initialize() {
        try (Connection conn = DatabaseManager.getConnection()) {
            executeSchemaCreation(conn);
            logger.info("Database was correctly initialized");
        } catch (SQLException e) {
            logger.error("Initialization error", e);
            throw new IllegalStateException("Database could not be initialized.", e);
        }
    }

    private static void executeSchemaCreation(Connection conn) throws SQLException {
        executeSql(conn, createAdvisorsSql());
        executeSql(conn, createCustomersSql());
        executeSql(conn, createCustomerCvProfilesSql());
        executeSql(conn, createFlightsSql());
        executeSql(conn, createFeedbacksSql());
        executeSql(conn, createFeedbackItemsSql());
        executeSql(conn, createIncidentsSql());
        executeSql(conn, createActionItemsSql());
        executeSql(conn, createCustomerNotesSql());
    }

    private static String createCustomersSql() {
        return """
            CREATE TABLE IF NOT EXISTS customers (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                email TEXT UNIQUE,
                birth_date TEXT,
                status TEXT,
                is_returning INTEGER DEFAULT 0,
                assigned_advisor_id INTEGER,
                preferences TEXT,
                apply_to_next_booking TEXT,

                FOREIGN KEY (assigned_advisor_id) REFERENCES advisors(id)
                    ON DELETE SET NULL
            );
            """;
    }

    private static String createCustomerCvProfilesSql() {
        return """
            CREATE TABLE IF NOT EXISTS customer_cv_profiles (
                customer_id INTEGER PRIMARY KEY,
                marketing_purpose INTEGER DEFAULT 0,
                newsletter_subscription INTEGER DEFAULT 0,
                referral_code INTEGER DEFAULT 0,
                payment_method TEXT,
                public_person INTEGER DEFAULT 0,
                customer_type TEXT,

                FOREIGN KEY (customer_id) REFERENCES customers(id)
                    ON DELETE CASCADE
            );
            """;
    }

    private static String createIncidentsSql() {
        return """
            CREATE TABLE IF NOT EXISTS incidents (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER,
                type TEXT,
                feedback_id INTEGER,
                feedback_type TEXT,
                description TEXT,
                score_impact REAL DEFAULT 0.0,
                revenue_risk INTEGER DEFAULT 0,
                status TEXT,
                assigned_advisor_id INTEGER,
                source_feedback_item_id INTEGER,
                delay_minutes INTEGER,
                flight_id INTEGER DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                FOREIGN KEY (customer_id) REFERENCES customers(id)
                    ON DELETE CASCADE,
                FOREIGN KEY (feedback_id) REFERENCES feedbacks(id)
                    ON DELETE SET NULL,
                FOREIGN KEY (flight_id) REFERENCES flights(id)
                    ON DELETE SET NULL,
                FOREIGN KEY (assigned_advisor_id) REFERENCES advisors(id)
                    ON DELETE SET NULL,
                FOREIGN KEY (source_feedback_item_id) REFERENCES feedback_items(id)
                    ON DELETE SET NULL
            );
            """;
    }

    private static String createFeedbacksSql() {
        return """
            CREATE TABLE IF NOT EXISTS feedbacks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                total_score REAL DEFAULT 0.0,
                customer_sat_score INTEGER DEFAULT 0,
                referral_score INTEGER DEFAULT 0,
                flight_id INTEGER DEFAULT 0,

                FOREIGN KEY (customer_id) REFERENCES customers(id)
                    ON DELETE CASCADE,
                FOREIGN KEY (flight_id) REFERENCES flights(id)
                    ON DELETE CASCADE
            );
            """;
    }

    private static String createFeedbackItemsSql() {
        return """
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
    }

    private static String createAdvisorsSql() {
        return """
            CREATE TABLE IF NOT EXISTS advisors (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                email TEXT UNIQUE,
                speciality TEXT,
                workload_score REAL DEFAULT 0.0
            );
            """;
    }

    private static String createActionItemsSql() {
        return """
            CREATE TABLE IF NOT EXISTS action_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                incident_id INTEGER NOT NULL,
                description TEXT NOT NULL,
                suggestion_1 TEXT,
                suggestion_2 TEXT,
                status TEXT NOT NULL,
                score_impact REAL DEFAULT 0.0,
                expected_rec REAL DEFAULT 0.0,
                expected_rebooking REAL DEFAULT 0.0,

                FOREIGN KEY (incident_id) REFERENCES incidents(id)
                    ON DELETE CASCADE
            );
            """;
    }

    private static String createFlightsSql() {
        return """
            CREATE TABLE IF NOT EXISTS flights (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER NOT NULL,
                flight_number TEXT,
                booking_date TEXT,
                flight_date TEXT,
                booking_package TEXT,
                status TEXT,
                is_current INTEGER DEFAULT 0,

                FOREIGN KEY (customer_id) REFERENCES customers(id)
                    ON DELETE CASCADE
            );
            """;
    }

    private static String createCustomerNotesSql() {
        return """
            CREATE TABLE IF NOT EXISTS customer_notes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER NOT NULL,
                advisor_id INTEGER NOT NULL,
                note_text TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                FOREIGN KEY (customer_id) REFERENCES customers(id)
                    ON DELETE CASCADE,
                FOREIGN KEY (advisor_id) REFERENCES advisors(id)
                    ON DELETE CASCADE
            );
            """;
    }

    private static void executeSql(Connection conn, String sql) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        }
    }
}
