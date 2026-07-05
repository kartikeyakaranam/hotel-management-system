package com.hotel.dao;

import com.hotel.db.DatabaseConnection;
import com.hotel.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, room_type, price_per_day, is_available) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, room.getRoomNumber());
            ps.setString(2, room.getRoomType());
            ps.setDouble(3, room.getPricePerDay());
            ps.setBoolean(4, room.isAvailable());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[RoomDAO] addRoom error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateRoom(Room room) {
        String sql = "UPDATE rooms SET room_type=?, price_per_day=?, is_available=? WHERE room_number=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getRoomType());
            ps.setDouble(2, room.getPricePerDay());
            ps.setBoolean(3, room.isAvailable());
            ps.setInt(4, room.getRoomNumber());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[RoomDAO] updateRoom error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteRoom(int roomNumber) {
        String sql = "DELETE FROM rooms WHERE room_number=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomNumber);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[RoomDAO] deleteRoom error: " + e.getMessage());
            return false;
        }
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[RoomDAO] getAllRooms error: " + e.getMessage());
        }
        return rooms;
    }

    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE is_available = TRUE ORDER BY room_number";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[RoomDAO] getAvailableRooms error: " + e.getMessage());
        }
        return rooms;
    }

    public Room getRoomByNumber(int roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[RoomDAO] getRoomByNumber error: " + e.getMessage());
        }
        return null;
    }

    public boolean setAvailability(int roomNumber, boolean available) {
        String sql = "UPDATE rooms SET is_available=? WHERE room_number=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setInt(2, roomNumber);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[RoomDAO] setAvailability error: " + e.getMessage());
            return false;
        }
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        return new Room(
            rs.getInt("room_number"),
            rs.getString("room_type"),
            rs.getDouble("price_per_day"),
            rs.getBoolean("is_available")
        );
    }
}
