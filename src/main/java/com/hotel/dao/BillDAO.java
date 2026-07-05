package com.hotel.dao;

import com.hotel.db.DatabaseConnection;
import com.hotel.model.Bill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    public boolean generateBill(Bill bill) {
        String sql = """
            INSERT INTO bills (booking_id, customer_id, room_number, num_days,
                               room_charges, service_tax, total_amount, bill_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_DATE)
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bill.getBookingId());
            ps.setInt(2, bill.getCustomerId());
            ps.setInt(3, bill.getRoomNumber());
            ps.setInt(4, bill.getNumDays());
            ps.setDouble(5, bill.getRoomCharges());
            ps.setDouble(6, bill.getServiceTax());
            ps.setDouble(7, bill.getTotalAmount());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[BillDAO] generateBill error: " + e.getMessage());
            return false;
        }
    }

    public List<Bill> getAllBills() {
        List<Bill> list = new ArrayList<>();
        String sql = """
            SELECT bl.*, c.name AS customer_name
            FROM bills bl
            JOIN customers c ON bl.customer_id = c.customer_id
            ORDER BY bl.bill_id DESC
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Bill b = mapRow(rs);
                b.setCustomerName(rs.getString("customer_name"));
                list.add(b);
            }
        } catch (SQLException e) {
            System.err.println("[BillDAO] getAllBills error: " + e.getMessage());
        }
        return list;
    }

    public Bill getBillByBookingId(int bookingId) {
        String sql = """
            SELECT bl.*, c.name AS customer_name
            FROM bills bl
            JOIN customers c ON bl.customer_id = c.customer_id
            WHERE bl.booking_id = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Bill b = mapRow(rs);
                b.setCustomerName(rs.getString("customer_name"));
                return b;
            }
        } catch (SQLException e) {
            System.err.println("[BillDAO] getBillByBookingId error: " + e.getMessage());
        }
        return null;
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        return new Bill(
            rs.getInt("bill_id"),
            rs.getInt("booking_id"),
            rs.getInt("customer_id"),
            rs.getInt("room_number"),
            rs.getInt("num_days"),
            rs.getDouble("room_charges"),
            rs.getDouble("service_tax"),
            rs.getDouble("total_amount"),
            rs.getString("bill_date")
        );
    }
}
