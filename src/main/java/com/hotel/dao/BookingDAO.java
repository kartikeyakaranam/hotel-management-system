package com.hotel.dao;

import com.hotel.db.DatabaseConnection;
import com.hotel.model.Booking;
import com.hotel.model.BookingStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public int addBooking(Booking b) {
        return addBookingWithDates(b, LocalDate.now());
    }

    public int addBookingWithDates(Booking b, LocalDate checkIn) {
        String sql = """
            INSERT INTO bookings (customer_id, room_number, check_in, num_days, status)
            VALUES (?, ?, ?, ?, ?)
            RETURNING booking_id
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, b.getCustomerId());
            ps.setInt(2, b.getRoomNumber());
            ps.setDate(3, Date.valueOf(checkIn));
            ps.setInt(4, b.getNumDays());
            ps.setString(5, BookingStatus.ACTIVE.getDbValue()); // ← enum
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[BookingDAO] addBookingWithDates error: " + e.getMessage());
        }
        return -1;
    }

    public boolean checkoutBooking(int bookingId) {
        String sql = """
            UPDATE bookings
            SET check_out = CURRENT_DATE,
                status    = ?
            WHERE booking_id = ?
              AND status     = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, BookingStatus.CHECKED_OUT.getDbValue()); // ← enum
            ps.setInt(2, bookingId);
            ps.setString(3, BookingStatus.ACTIVE.getDbValue());      // ← enum
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookingDAO] checkoutBooking error: " + e.getMessage());
            return false;
        }
    }

    public List<Booking> getAllBookings() {
        return fetchBookings("""
            SELECT b.*, c.name AS customer_name, r.room_type
            FROM bookings b
            JOIN customers c ON b.customer_id = c.customer_id
            JOIN rooms     r ON b.room_number  = r.room_number
            ORDER BY b.booking_id DESC
            """);
    }

    public List<Booking> getActiveBookings() {
        return fetchBookingsByStatus(BookingStatus.ACTIVE);
    }

    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return fetchBookingsByStatus(status);
    }

    public Booking getBookingById(int id) {
        String sql = """
            SELECT b.*, c.name AS customer_name, r.room_type
            FROM bookings b
            JOIN customers c ON b.customer_id = c.customer_id
            JOIN rooms     r ON b.room_number  = r.room_number
            WHERE b.booking_id = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[BookingDAO] getBookingById error: " + e.getMessage());
        }
        return null;
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private List<Booking> fetchBookingsByStatus(BookingStatus status) {
        String sql = """
            SELECT b.*, c.name AS customer_name, r.room_type
            FROM bookings b
            JOIN customers c ON b.customer_id = c.customer_id
            JOIN rooms     r ON b.room_number  = r.room_number
            WHERE b.status = ?
            ORDER BY b.booking_id DESC
            """;
        List<Booking> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.getDbValue());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookingDAO] fetchBookingsByStatus error: " + e.getMessage());
        }
        return list;
    }

    private List<Booking> fetchBookings(String sql) {
        List<Booking> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookingDAO] fetchBookings error: " + e.getMessage());
        }
        return list;
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking bk = new Booking(
            rs.getInt("booking_id"),
            rs.getInt("customer_id"),
            rs.getInt("room_number"),
            rs.getString("check_in"),
            rs.getString("check_out"),
            rs.getInt("num_days"),
            rs.getString("status")
        );
        bk.setCustomerName(rs.getString("customer_name"));
        bk.setRoomType(rs.getString("room_type"));
        return bk;
    }
}
