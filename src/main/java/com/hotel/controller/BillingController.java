package com.hotel.controller;

import com.hotel.dao.BillDAO;
import com.hotel.dao.BookingDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.model.Bill;
import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BillingController implements Initializable {

    private static final double TAX_RATE = 0.12; // 12% GST

    // ── Generate bill ────────────────────────────────────────────────────────
    @FXML private TextField      txtBookingId;
    @FXML private Label          lblCustomer;
    @FXML private Label          lblRoom;
    @FXML private Label          lblDays;
    @FXML private Label          lblRoomCharges;
    @FXML private Label          lblTax;
    @FXML private Label          lblTotal;

    // ── Bills table ──────────────────────────────────────────────────────────
    @FXML private TableView<Bill>           tblBills;
    @FXML private TableColumn<Bill,Integer> colBillId;
    @FXML private TableColumn<Bill,Integer> colBookingId;
    @FXML private TableColumn<Bill,String>  colCustomer;
    @FXML private TableColumn<Bill,Integer> colRoom;
    @FXML private TableColumn<Bill,Integer> colDaysCol;
    @FXML private TableColumn<Bill,Double>  colCharges;
    @FXML private TableColumn<Bill,Double>  colTaxCol;
    @FXML private TableColumn<Bill,Double>  colTotal;
    @FXML private TableColumn<Bill,String>  colDate;

    private final BillDAO    billDAO    = new BillDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final RoomDAO    roomDAO    = new RoomDAO();

    private final ObservableList<Bill> billList = FXCollections.observableArrayList();
    private Bill previewBill = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colBillId.setCellValueFactory(new PropertyValueFactory<>("billId"));
        colBookingId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colRoom.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colDaysCol.setCellValueFactory(new PropertyValueFactory<>("numDays"));
        colCharges.setCellValueFactory(new PropertyValueFactory<>("roomCharges"));
        colTaxCol.setCellValueFactory(new PropertyValueFactory<>("serviceTax"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("billDate"));

        tblBills.setItems(billList);
        loadBills();
    }

    @FXML
    private void handlePreview() {
        String idStr = txtBookingId.getText().trim();
        if (idStr.isEmpty()) { AlertUtil.showWarning("Input", "Enter a Booking ID."); return; }
        int bookingId;
        try { bookingId = Integer.parseInt(idStr); }
        catch (NumberFormatException e) { AlertUtil.showError("Invalid", "Booking ID must be a number."); return; }

        // Check if bill already exists
        Bill existing = billDAO.getBillByBookingId(bookingId);
        if (existing != null) {
            AlertUtil.showWarning("Already Billed", "Bill already generated for Booking #" + bookingId);
            return;
        }

        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null) { AlertUtil.showError("Not Found", "No booking found for ID: " + bookingId); return; }
        if ("ACTIVE".equals(booking.getStatus())) {
            AlertUtil.showWarning("Active Booking", "Please check out the guest before generating a bill.");
            return;
        }

        Room room = roomDAO.getRoomByNumber(booking.getRoomNumber());
        if (room == null) { AlertUtil.showError("Error", "Room not found."); return; }

        double roomCharges = room.getPricePerDay() * booking.getNumDays();
        double tax = roomCharges * TAX_RATE;
        double total = roomCharges + tax;

        previewBill = new Bill(0, bookingId, booking.getCustomerId(),
            booking.getRoomNumber(), booking.getNumDays(),
            roomCharges, tax, total, "");
        previewBill.setCustomerName(booking.getCustomerName());

        lblCustomer.setText(booking.getCustomerName() + "  (ID: " + booking.getCustomerId() + ")");
        lblRoom.setText("Room #" + booking.getRoomNumber() + " – " + booking.getRoomType()
            + "  @₹" + room.getPricePerDay() + "/day");
        lblDays.setText(String.valueOf(booking.getNumDays()));
        lblRoomCharges.setText(String.format("₹ %.2f", roomCharges));
        lblTax.setText(String.format("₹ %.2f  (12%% GST)", tax));
        lblTotal.setText(String.format("₹ %.2f", total));
    }

    @FXML
    private void handleGenerateBill() {
        if (previewBill == null) { AlertUtil.showWarning("Preview First", "Click 'Preview Bill' first."); return; }
        if (billDAO.generateBill(previewBill)) {
            AlertUtil.showInfo("Bill Generated", String.format(
                "Bill saved!\nTotal Amount: ₹ %.2f", previewBill.getTotalAmount()));
            previewBill = null;
            clearPreview();
            loadBills();
        } else {
            AlertUtil.showError("Error", "Failed to generate bill.");
        }
    }

    @FXML
    private void handleExportCSV() {
        List<Bill> bills = billDAO.getAllBills();
        if (bills.isEmpty()) { AlertUtil.showWarning("Empty", "No bills to export."); return; }

        FileChooser fc = new FileChooser();
        fc.setTitle("Save Bills CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fc.setInitialFileName("hotel_bills.csv");
        File file = fc.showSaveDialog(tblBills.getScene().getWindow());
        if (file == null) return;

        try (FileWriter fw = new FileWriter(file)) {
            fw.write("Bill ID,Booking ID,Customer,Room No,Days,Room Charges,Tax,Total,Date\n");
            for (Bill b : bills) {
                fw.write(String.format("%d,%d,%s,%d,%d,%.2f,%.2f,%.2f,%s\n",
                    b.getBillId(), b.getBookingId(), b.getCustomerName(),
                    b.getRoomNumber(), b.getNumDays(),
                    b.getRoomCharges(), b.getServiceTax(),
                    b.getTotalAmount(), b.getBillDate()));
            }
            AlertUtil.showInfo("Exported", "Bills exported to:\n" + file.getAbsolutePath());
        } catch (IOException e) {
            AlertUtil.showError("Export Error", e.getMessage());
        }
    }

    private void loadBills() { billList.setAll(billDAO.getAllBills()); }

    private void clearPreview() {
        txtBookingId.clear();
        lblCustomer.setText("–"); lblRoom.setText("–");
        lblDays.setText("–"); lblRoomCharges.setText("–");
        lblTax.setText("–"); lblTotal.setText("–");
    }
}
