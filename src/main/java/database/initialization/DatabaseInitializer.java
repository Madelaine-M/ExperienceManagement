package database.initialization;

import database.connection.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
                booking_date TEXT,
                is_returning INTEGER DEFAULT 0,
                assigned_advisor_id INTEGER,
                clv_score REAL DEFAULT 0.0,
                notes TEXT,
                preferences TEXT,
                apply_to_next_booking TEXT,
                marketing_purpose INTEGER DEFAULT 0,
                newsletter_subscription INTEGER DEFAULT 0,
                referral_code INTEGER DEFAULT 0,
                payment_method TEXT,
                public_person INTEGER DEFAULT 0,
                customer_type TEXT,

                FOREIGN KEY (assigned_advisor_id) REFERENCES advisors(id)
                    ON DELETE SET NULL
            );
            """;
        String createIncidents = """
            CREATE TABLE IF NOT EXISTS incidents (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER,
                type TEXT,
                feedback_type TEXT,
                description TEXT,
                priority_score REAL DEFAULT 0.0,
                score_impact REAL DEFAULT 0.0,
                revenue_risk INTEGER DEFAULT 0,
                status TEXT,
                assigned_advisor_id INTEGER,
                source_feedback_item_id INTEGER,
                flight_id INTEGER DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                FOREIGN KEY (customer_id) REFERENCES customers(id)
                    ON DELETE CASCADE,
                FOREIGN KEY (flight_id) REFERENCES flights(id)
                    ON DELETE SET NULL,
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
                total_score REAL DEFAULT 0.0,
                customer_sat_score INTEGER DEFAULT 0,
                flight_id INTEGER DEFAULT 0,

                FOREIGN KEY (customer_id) REFERENCES customers(id)
                    ON DELETE CASCADE,
                FOREIGN KEY (flight_id) REFERENCES flights(id)
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
                suggestion_1 TEXT,
                suggestion_2 TEXT,
                status TEXT NOT NULL,
                priority INTEGER NOT NULL,
                score_impact REAL DEFAULT 0.0,
                expected_rec REAL DEFAULT 0.0,
                expected_rebooking REAL DEFAULT 0.0,

                FOREIGN KEY (incident_id) REFERENCES incidents(id)
                    ON DELETE CASCADE
            );
            """;
        String createFlights = """
            CREATE TABLE IF NOT EXISTS flights (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER NOT NULL,
                flight_number TEXT,
                flight_date TEXT,
                booking_package TEXT,
                status TEXT,
                is_current INTEGER DEFAULT 0,

                FOREIGN KEY (customer_id) REFERENCES customers(id)
                    ON DELETE CASCADE
            );
            """;
        try (Connection conn = DatabaseManager.getConnection()) {

            executeSql(conn, createAdvisors);
            executeSql(conn, createCustomers);
            executeSql(conn, createFlights);
            executeSql(conn, createFeedbacks);
            executeSql(conn, createFeedbackItems);
            executeSql(conn, createIncidents);
            executeSql(conn, createActionItems);
            ensureCustomerColumnsExist(conn);
            ensureFlightColumnsExist(conn);
            ensureFeedbackColumnsExist(conn);
            ensureIncidentColumnsExist(conn);
            ensureActionItemColumnsExist(conn);

            logger.info("Database was correctly initialized");

        } catch (Exception e) {
            logger.error("Initialization error", e);
        }
    }

    private static void ensureIncidentColumnsExist(Connection conn) throws SQLException {
        if (!columnExists(conn, "incidents", "feedback_type")) {
            executeSql(conn, """
                ALTER TABLE incidents
                ADD COLUMN feedback_type TEXT;
                """);
        }

        if (!columnExists(conn, "incidents", "revenue_risk")) {
            executeSql(conn, """
                ALTER TABLE incidents
                ADD COLUMN revenue_risk INTEGER DEFAULT 0;
                """);
        }

        if (!columnExists(conn, "incidents", "assigned_advisor_id")) {
            executeSql(conn, """
                ALTER TABLE incidents
                ADD COLUMN assigned_advisor_id INTEGER;
                """);
        }

        if (!columnExists(conn, "incidents", "source_feedback_item_id")) {
            executeSql(conn, """
                ALTER TABLE incidents
                ADD COLUMN source_feedback_item_id INTEGER;
                """);
        }

        if (!columnExists(conn, "incidents", "flight_id")) {
            executeSql(conn, """
                ALTER TABLE incidents
                ADD COLUMN flight_id INTEGER DEFAULT 0;
                """);
        }
    }

    private static void ensureFlightColumnsExist(Connection conn) throws SQLException {
        if (!columnExists(conn, "flights", "flight_number")) {
            executeSql(conn, """
                ALTER TABLE flights
                ADD COLUMN flight_number TEXT;
                """);
        }

        if (!columnExists(conn, "flights", "flight_date")) {
            executeSql(conn, """
                ALTER TABLE flights
                ADD COLUMN flight_date TEXT;
                """);
        }

        if (!columnExists(conn, "flights", "booking_package")) {
            executeSql(conn, """
                ALTER TABLE flights
                ADD COLUMN booking_package TEXT;
                """);
        }

        if (!columnExists(conn, "flights", "status")) {
            executeSql(conn, """
                ALTER TABLE flights
                ADD COLUMN status TEXT;
                """);
        }

        if (!columnExists(conn, "flights", "is_current")) {
            executeSql(conn, """
                ALTER TABLE flights
                ADD COLUMN is_current INTEGER DEFAULT 0;
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

        if (!columnExists(conn, "customers", "notes")) {
            executeSql(conn, """
                ALTER TABLE customers
                ADD COLUMN notes TEXT;
                """);
        }

        if (!columnExists(conn, "customers", "preferences")) {
            executeSql(conn, """
                ALTER TABLE customers
                ADD COLUMN preferences TEXT;
                """);
        }

        if (!columnExists(conn, "customers", "apply_to_next_booking")) {
            executeSql(conn, """
                ALTER TABLE customers
                ADD COLUMN apply_to_next_booking TEXT;
                """);
        }

        if (!columnExists(conn, "customers", "marketing_purpose")) {
            executeSql(conn, """
                ALTER TABLE customers
                ADD COLUMN marketing_purpose INTEGER DEFAULT 0;
                """);
        }

        if (!columnExists(conn, "customers", "newsletter_subscription")) {
            executeSql(conn, """
                ALTER TABLE customers
                ADD COLUMN newsletter_subscription INTEGER DEFAULT 0;
                """);
        }

        if (!columnExists(conn, "customers", "referral_code")) {
            executeSql(conn, """
                ALTER TABLE customers
                ADD COLUMN referral_code INTEGER DEFAULT 0;
                """);
        }

        if (!columnExists(conn, "customers", "payment_method")) {
            executeSql(conn, """
                ALTER TABLE customers
                ADD COLUMN payment_method TEXT;
                """);
        }

        if (!columnExists(conn, "customers", "public_person")) {
            executeSql(conn, """
                ALTER TABLE customers
                ADD COLUMN public_person INTEGER DEFAULT 0;
                """);
        }

        if (!columnExists(conn, "customers", "customer_type")) {
            executeSql(conn, """
                ALTER TABLE customers
                ADD COLUMN customer_type TEXT;
                """);
        }

    }

    private static void ensureFeedbackColumnsExist(Connection conn) throws SQLException {
        if (!columnExists(conn, "feedbacks", "total_score")) {
            executeSql(conn, """
                ALTER TABLE feedbacks
                ADD COLUMN total_score REAL DEFAULT 0.0;
                """);
        }

        if (!columnExists(conn, "feedbacks", "customer_sat_score")) {
            executeSql(conn, """
                ALTER TABLE feedbacks
                ADD COLUMN customer_sat_score INTEGER DEFAULT 0;
                """);
        }

        if (!columnExists(conn, "feedbacks", "flight_id")) {
            executeSql(conn, """
                ALTER TABLE feedbacks
                ADD COLUMN flight_id INTEGER DEFAULT 0;
                """);
        }
    }

    private static void ensureActionItemColumnsExist(Connection conn) throws SQLException {
        if (!columnExists(conn, "action_items", "suggestion_1")) {
            executeSql(conn, """
                ALTER TABLE action_items
                ADD COLUMN suggestion_1 TEXT;
                """);
        }

        if (!columnExists(conn, "action_items", "suggestion_2")) {
            executeSql(conn, """
                ALTER TABLE action_items
                ADD COLUMN suggestion_2 TEXT;
                """);
        }

        if (!columnExists(conn, "action_items", "score_impact")) {
            executeSql(conn, """
                ALTER TABLE action_items
                ADD COLUMN score_impact REAL DEFAULT 0.0;
                """);
        }

        if (!columnExists(conn, "action_items", "expected_rec")) {
            executeSql(conn, """
                ALTER TABLE action_items
                ADD COLUMN expected_rec REAL DEFAULT 0.0;
                """);
        }

        if (!columnExists(conn, "action_items", "expected_rebooking")) {
            executeSql(conn, """
                ALTER TABLE action_items
                ADD COLUMN expected_rebooking REAL DEFAULT 0.0;
                """);
        }
    }

    private static void executeSql(Connection conn, String sql) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        }
    }

    private static boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + tableName + ")")) {

            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
        }

        return false;
    }
}
