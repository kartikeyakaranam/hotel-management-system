package com.hotel.dao;

import com.hotel.db.DatabaseConnection;
import com.hotel.model.BookingStatus;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides aggregated statistics for the Dashboard tab.
 */
public class DashboardDAO {

    public int getTotalRooms() {
        return queryInt("SELECT COUNT(*) FROM rooms");
    }

    public int getOccupiedRooms() {
        return queryInt("SELECT COUNT(*) FROM rooms WHERE is_available = FALSE");
    }

    public int getAvailableRooms() {
        return queryInt("SELECT COUNT(*) FROM rooms WHERE is_available = TRUE");
    }

    public int getActiveBookings() {
        return queryInt("SELECT COUNT(*) FROM bookings WHERE status = '"
            + BookingStatus.ACTIVE.getDbValue() + "'");
    }

    public int getTotalCustomers() {
        return queryInt("SELECT COUNT(*) FROM customers");
    }

    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount),0) FROM bills";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s  = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[DashboardDAO] getTotalRevenue: " + e.getMessage());
        }
        return 0;
    }

    /** Returns room-type → count of available rooms */
    public Map<String, Integer> getAvailableByType() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = """
            SELECT room_type, COUNT(*) AS cnt
            FROM rooms
            WHERE is_available = TRUE
            GROUP BY room_type
            ORDER BY room_type
            """;
        try (Connection c = DatabaseConnection.getConnection();
             Statement s  = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString("room_type"), rs.getInt("cnt"));
        } catch (SQLException e) {
            System.err.println("[DashboardDAO] getAvailableByType: " + e.getMessage());
        }
        return map;
    }

    /** Returns room-type → count of occupied rooms */
    public Map<String, Integer> getOccupiedByType() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = """
            SELECT room_type, COUNT(*) AS cnt
            FROM rooms
            WHERE is_available = FALSE
            GROUP BY room_type
            ORDER BY room_type
            """;
        try (Connection c = DatabaseConnection.getConnection();
             Statement s  = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString("room_type"), rs.getInt("cnt"));
        } catch (SQLException e) {
            System.err.println("[DashboardDAO] getOccupiedByType: " + e.getMessage());
        }
        return map;
    }

    // ── Helper ────────────────────────────────────────────────────────────
    private int queryInt(String sql) {
        try (Connection c = DatabaseConnection.getConnection();
             Statement s  = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[DashboardDAO] queryInt error: " + e.getMessage());
        }
        return 0;
    }
}
