package com.hotel.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton PostgreSQL connection manager.
 *
 * Default:  hms_pro_db @ localhost:5432  /  user: postgres  /  pass: postgres
 * Change USERNAME / PASSWORD to match your local PostgreSQL setup.
 *
 * For a brand-new database run the bundled  hms_pro_db_setup.sql  script:
 *   psql -U postgres -c "CREATE DATABASE hms_pro_db;"
 *   psql -U postgres -d hms_pro_db -f hms_pro_db_setup.sql
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:postgresql://localhost:5432/hms_pro_db";
    private static final String USERNAME = System.getenv().getOrDefault("HMS_DB_USER", "postgres");
    private static final String PASSWORD = System.getenv().getOrDefault("HMS_DB_PASSWORD", "");

    private static Connection connection = null;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        return connection;
    }

    /**
     * Creates all required tables if they don't already exist.
     * Includes DB-level constraints that mirror the application-level checks:
     *   • contact  : exactly 10 digits
     *   • num_days : > 0
     *   • check_out: must be AFTER check_in
     */
    public static void initializeDatabase() {
        String createRooms = """
            CREATE TABLE IF NOT EXISTS rooms (
                room_number   INT PRIMARY KEY,
                room_type     VARCHAR(50)   NOT NULL,
                price_per_day NUMERIC(10,2) NOT NULL CHECK (price_per_day > 0),
                is_available  BOOLEAN NOT NULL DEFAULT TRUE
            );
            """;

        String createCustomers = """
            CREATE TABLE IF NOT EXISTS customers (
                customer_id  SERIAL PRIMARY KEY,
                name         VARCHAR(100) NOT NULL,
                contact      VARCHAR(10)  NOT NULL
                                 CHECK (contact ~ '^\\d{10}$'),
                email        VARCHAR(100),
                address      TEXT
            );
            """;

        String createBookings = """
            CREATE TABLE IF NOT EXISTS bookings (
                booking_id   SERIAL PRIMARY KEY,
                customer_id  INT  NOT NULL REFERENCES customers(customer_id) ON DELETE CASCADE,
                room_number  INT  NOT NULL REFERENCES rooms(room_number)     ON DELETE RESTRICT,
                check_in     DATE NOT NULL DEFAULT CURRENT_DATE,
                check_out    DATE,
                num_days     INT  NOT NULL DEFAULT 1 CHECK (num_days >= 1),
                status       VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                                 CHECK (status IN ('ACTIVE','CHECKED_OUT','CANCELLED')),
                CONSTRAINT chk_checkout_not_before_checkin
                    CHECK (check_out IS NULL OR check_out >= check_in)
            );
            """;

        String createBills = """
            CREATE TABLE IF NOT EXISTS bills (
                bill_id       SERIAL PRIMARY KEY,
                booking_id    INT  NOT NULL REFERENCES bookings(booking_id) ON DELETE RESTRICT,
                customer_id   INT  NOT NULL REFERENCES customers(customer_id),
                room_number   INT  NOT NULL,
                num_days      INT  NOT NULL CHECK (num_days >= 1),
                room_charges  NUMERIC(10,2) NOT NULL CHECK (room_charges >= 0),
                service_tax   NUMERIC(10,2) NOT NULL CHECK (service_tax  >= 0),
                total_amount  NUMERIC(10,2) NOT NULL CHECK (total_amount >= 0),
                bill_date     DATE NOT NULL DEFAULT CURRENT_DATE
            );
            """;

        try (Connection conn = getConnection();
             Statement  stmt = conn.createStatement()) {

            stmt.executeUpdate(createRooms);
            stmt.executeUpdate(createCustomers);
            stmt.executeUpdate(createBookings);
            stmt.executeUpdate(createBills);
            System.out.println("[DB] All tables initialized with validation constraints.");

        } catch (SQLException e) {
            System.err.println("[DB] Init error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
