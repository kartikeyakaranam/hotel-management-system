package com.hotel.controller;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.CustomerDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.model.Booking;
import com.hotel.model.BookingStatus;
import com.hotel.model.Customer;
import com.hotel.model.Room;
import com.hotel.util.AlertUtil;
import com.hotel.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

public class BookingController implements Initializable {

    @FXML private ComboBox<Customer> cmbCustomer;
    @FXML private ComboBox<Room>     cmbRoom;
    @FXML private DatePicker         dpCheckIn;
    @FXML private DatePicker         dpCheckOut;
    @FXML private TextField          txtNumDays;

    @FXML private TableView<Booking>           tblBookings;
    @FXML private TableColumn<Booking,Integer> colBookingId;
    @FXML private TableColumn<Booking,String>  colCustomer;
    @FXML private TableColumn<Booking,Integer> colRoom;
    @FXML private TableColumn<Booking,String>  colRoomType;
    @FXML private TableColumn<Booking,String>  colCheckIn;
    @FXML private TableColumn<Booking,String>  colCheckOut;
    @FXML private TableColumn<Booking,Integer> colDays;
    @FXML private TableColumn<Booking,String>  colStatus;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final BookingDAO  bookingDAO  = new BookingDAO();
    private final RoomDAO     roomDAO     = new RoomDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    private final ObservableList<Booking> bookingList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colBookingId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colRoom.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colRoomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        colCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOut"));
        colDays.setCellValueFactory(new PropertyValueFactory<>("numDays"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        tblBookings.setItems(bookingList);
        refreshComboBoxes();
        loadBookings();

        dpCheckIn.setValue(LocalDate.now());
        dpCheckIn.valueProperty().addListener((obs, o, n)  -> autoFillDays());
        dpCheckOut.valueProperty().addListener((obs, o, n) -> autoFillDays());
    }

    @FXML
    private void handleBook() {
        Customer customer = cmbCustomer.getValue();
        Room room         = cmbRoom.getValue();

        if (customer == null || room == null) {
            AlertUtil.showWarning("Validation", "Please select both a customer and a room.");
            return;
        }

        LocalDate checkIn = dpCheckIn.getValue();
        if (checkIn == null) {
            AlertUtil.showWarning("Validation Error", "Check-In date is required.");
            return;
        }

        LocalDate checkOut = dpCheckOut.getValue();
        if (checkOut == null) {
            AlertUtil.showWarning("Validation Error", "Check-Out date is required.");
            return;
        }

        // ── Same-day booking = 1 day; checkout must not be BEFORE check-in ──
        if (checkOut.isBefore(checkIn)) {
            AlertUtil.showWarning("Invalid Dates",
                "Check-Out date cannot be before Check-In date.\n" +
                "Check-In:  " + checkIn + "\n" +
                "Check-Out: " + checkOut);
            return;
        }

        // If same day → 1 day stay
        long daysBetween = ChronoUnit.DAYS.between(checkIn, checkOut);
        int days = (daysBetween == 0) ? 1 : (int) daysBetween;

        if (!ValidationUtil.isValidDays(days)) {
            AlertUtil.showError("Invalid Days", "Number of days must be at least 1.");
            return;
        }

        if (!room.isAvailable()) {
            AlertUtil.showError("Unavailable",
                "Room #" + room.getRoomNumber() + " is already occupied.");
            return;
        }

        Booking booking = new Booking();
        booking.setCustomerId(customer.getCustomerId());
        booking.setRoomNumber(room.getRoomNumber());
        booking.setNumDays(days);
        booking.setCheckIn(checkIn.format(DATE_FMT));
        booking.setCheckOut("");
        booking.setStatusEnum(BookingStatus.ACTIVE);

        int id = bookingDAO.addBookingWithDates(booking, checkIn);
        if (id > 0) {
            roomDAO.setAvailability(room.getRoomNumber(), false);
            AlertUtil.showInfo("Booked!",
                "Booking confirmed!\n" +
                "Booking ID : " + id + "\n" +
                "Check-In   : " + checkIn + "\n" +
                "Check-Out  : " + checkOut + "\n" +
                "Days       : " + days +
                (daysBetween == 0 ? "  (same-day → counted as 1)" : ""));
            clearForm();
            refreshComboBoxes();
            loadBookings();
        } else {
            AlertUtil.showError("Error", "Booking failed. Please try again.");
        }
    }

    @FXML
    private void handleCheckout() {
        Booking sel = tblBookings.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertUtil.showWarning("Select", "Please select a booking to check out.");
            return;
        }
        // Use enum comparison — no more magic strings
        if (sel.getStatusEnum() != BookingStatus.ACTIVE) {
            AlertUtil.showWarning("Already Done",
                "Booking #" + sel.getBookingId() + " is already " +
                sel.getStatusEnum().getDisplayLabel() + ".");
            return;
        }

        if (AlertUtil.showConfirm("Checkout",
                "Check out Booking #" + sel.getBookingId() +
                " (Room #" + sel.getRoomNumber() + ")?")) {
            bookingDAO.checkoutBooking(sel.getBookingId());
            roomDAO.setAvailability(sel.getRoomNumber(), true);
            refreshComboBoxes();
            loadBookings();
            AlertUtil.showInfo("Checked Out",
                "Room #" + sel.getRoomNumber() + " is now available.");
        }
    }

    @FXML private void handleRefresh() { refreshComboBoxes(); loadBookings(); }

    private void autoFillDays() {
        LocalDate ci = dpCheckIn.getValue();
        LocalDate co = dpCheckOut.getValue();
        if (ci == null || co == null) {
            txtNumDays.clear();
            txtNumDays.setStyle("");
            return;
        }
        if (co.isBefore(ci)) {
            txtNumDays.setText("⚠ Invalid");
            txtNumDays.setStyle("-fx-border-color: #e05252;");
            return;
        }
        long d = ChronoUnit.DAYS.between(ci, co);
        int display = (d == 0) ? 1 : (int) d;
        txtNumDays.setText(display + (d == 0 ? "  (same-day)" : ""));
        txtNumDays.setStyle("-fx-border-color: #00c875;");
    }

    private void loadBookings()     { bookingList.setAll(bookingDAO.getAllBookings()); }

    private void refreshComboBoxes() {
        cmbCustomer.setItems(FXCollections.observableArrayList(customerDAO.getAllCustomers()));
        cmbRoom.setItems(FXCollections.observableArrayList(roomDAO.getAvailableRooms()));
    }

    private void clearForm() {
        cmbCustomer.getSelectionModel().clearSelection();
        cmbRoom.getSelectionModel().clearSelection();
        dpCheckIn.setValue(LocalDate.now());
        dpCheckOut.setValue(null);
        txtNumDays.clear();
        txtNumDays.setStyle("");
    }
}
