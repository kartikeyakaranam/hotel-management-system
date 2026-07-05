package com.hotel.controller;

import com.hotel.dao.RoomDAO;
import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RoomController implements Initializable {

    @FXML private TextField          txtRoomNumber;
    @FXML private ComboBox<RoomType> cmbRoomType;   // ← now typed to enum
    @FXML private TextField          txtPrice;
    @FXML private CheckBox           chkAvailable;

    @FXML private TableView<Room>            tblRooms;
    @FXML private TableColumn<Room,Integer>  colRoomNo;
    @FXML private TableColumn<Room,String>   colType;
    @FXML private TableColumn<Room,Double>   colPrice;
    @FXML private TableColumn<Room,String>   colStatus;

    @FXML private ToggleGroup filterGroup;
    @FXML private RadioButton rbAll;
    @FXML private RadioButton rbAvailable;

    private final RoomDAO roomDAO = new RoomDAO();
    private final ObservableList<Room> roomList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Populate combo from enum values
        cmbRoomType.setItems(FXCollections.observableArrayList(RoomType.values()));
        cmbRoomType.getSelectionModel().selectFirst();
        chkAvailable.setSelected(true);

        colRoomNo.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
        colStatus.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getStatusString()));

        tblRooms.setItems(roomList);
        loadRooms();

        tblRooms.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateForm(sel);
        });
    }

    @FXML private void handleAddRoom() {
        Room room = buildRoomFromForm();
        if (room == null) return;
        if (roomDAO.getRoomByNumber(room.getRoomNumber()) != null) {
            AlertUtil.showError("Duplicate", "Room #" + room.getRoomNumber() + " already exists.");
            return;
        }
        if (roomDAO.addRoom(room)) {
            AlertUtil.showInfo("Success", "Room added successfully.");
            clearForm(); loadRooms();
        } else {
            AlertUtil.showError("Error", "Failed to add room.");
        }
    }

    @FXML private void handleUpdateRoom() {
        Room room = buildRoomFromForm();
        if (room == null) return;
        if (roomDAO.updateRoom(room)) {
            AlertUtil.showInfo("Success", "Room updated successfully.");
            clearForm(); loadRooms();
        } else {
            AlertUtil.showError("Error", "Failed to update room.");
        }
    }

    @FXML private void handleDeleteRoom() {
        Room sel = tblRooms.getSelectionModel().getSelectedItem();
        if (sel == null)          { AlertUtil.showWarning("Select",   "Please select a room."); return; }
        if (!sel.isAvailable())   { AlertUtil.showWarning("Occupied", "Cannot delete an occupied room."); return; }
        if (AlertUtil.showConfirm("Delete", "Delete Room #" + sel.getRoomNumber() + "?")) {
            roomDAO.deleteRoom(sel.getRoomNumber());
            clearForm(); loadRooms();
        }
    }

    @FXML private void handleFilter() {
        roomList.setAll(rbAvailable.isSelected()
            ? roomDAO.getAvailableRooms()
            : roomDAO.getAllRooms());
    }

    @FXML private void handleClear() { clearForm(); }

    private Room buildRoomFromForm() {
        String noStr    = txtRoomNumber.getText().trim();
        String priceStr = txtPrice.getText().trim();
        RoomType type   = cmbRoomType.getValue();

        if (noStr.isEmpty() || priceStr.isEmpty() || type == null) {
            AlertUtil.showWarning("Validation", "Room number, type and price are required.");
            return null;
        }
        try {
            int    no    = Integer.parseInt(noStr);
            double price = Double.parseDouble(priceStr);
            if (price <= 0) throw new NumberFormatException("price <= 0");
            return new Room(no, type.getDisplayName(), price, chkAvailable.isSelected());
        } catch (NumberFormatException e) {
            AlertUtil.showError("Invalid Input",
                "Room number must be an integer and price must be a positive number.");
            return null;
        }
    }

    private void populateForm(Room r) {
        txtRoomNumber.setText(String.valueOf(r.getRoomNumber()));
        cmbRoomType.setValue(r.getRoomTypeEnum());
        txtPrice.setText(String.valueOf(r.getPricePerDay()));
        chkAvailable.setSelected(r.isAvailable());
    }

    private void loadRooms() { roomList.setAll(roomDAO.getAllRooms()); }

    private void clearForm() {
        txtRoomNumber.clear();
        txtPrice.clear();
        cmbRoomType.getSelectionModel().selectFirst();
        chkAvailable.setSelected(true);
        tblRooms.getSelectionModel().clearSelection();
    }
}
