package com.hotel.controller;

import com.hotel.dao.CustomerDAO;
import com.hotel.model.Customer;
import com.hotel.util.AlertUtil;
import com.hotel.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    @FXML private TextField txtName;
    @FXML private TextField txtContact;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAddress;

    @FXML private TableView<Customer>           tblCustomers;
    @FXML private TableColumn<Customer,Integer> colId;
    @FXML private TableColumn<Customer,String>  colName;
    @FXML private TableColumn<Customer,String>  colContact;
    @FXML private TableColumn<Customer,String>  colEmail;
    @FXML private TableColumn<Customer,String>  colAddress;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        tblCustomers.setItems(customerList);
        loadCustomers();

        tblCustomers.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateForm(sel);
        });

        // Live phone-field styling: turns red border when length != 10
        txtContact.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                txtContact.setStyle("");
            } else if (ValidationUtil.isValidPhone(newVal)) {
                txtContact.setStyle("-fx-border-color: #00c875; -fx-border-width: 1.5px;");
            } else {
                txtContact.setStyle("-fx-border-color: #e05252; -fx-border-width: 1.5px;");
            }
        });
    }

    @FXML
    private void handleAddCustomer() {
        Customer c = buildFromForm();
        if (c == null) return;
        if (customerDAO.addCustomer(c)) {
            AlertUtil.showInfo("Success", "Customer added successfully.");
            clearForm();
            loadCustomers();
        } else {
            AlertUtil.showError("Error", "Failed to add customer.");
        }
    }

    @FXML
    private void handleUpdateCustomer() {
        Customer sel = tblCustomers.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertUtil.showWarning("Select", "Please select a customer first.");
            return;
        }
        Customer c = buildFromForm();
        if (c == null) return;
        c.setCustomerId(sel.getCustomerId());
        if (customerDAO.updateCustomer(c)) {
            AlertUtil.showInfo("Success", "Customer updated successfully.");
            clearForm();
            loadCustomers();
        } else {
            AlertUtil.showError("Error", "Failed to update customer.");
        }
    }

    @FXML
    private void handleDeleteCustomer() {
        Customer sel = tblCustomers.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertUtil.showWarning("Select", "Please select a customer.");
            return;
        }
        if (AlertUtil.showConfirm("Delete", "Delete customer: " + sel.getName() + "?")) {
            customerDAO.deleteCustomer(sel.getCustomerId());
            clearForm();
            loadCustomers();
        }
    }

    @FXML
    private void handleClear() { clearForm(); }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void loadCustomers() {
        customerList.setAll(customerDAO.getAllCustomers());
    }

    /**
     * Reads and validates all form fields.
     * Returns null (and shows an alert) on any validation failure.
     */
    private Customer buildFromForm() {
        String name    = txtName.getText().trim();
        String contact = txtContact.getText().trim();
        String email   = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();

        // ── Required fields ──────────────────────────────────────────────────
        if (name.isEmpty()) {
            AlertUtil.showWarning("Validation Error", "Customer name is required.");
            txtName.requestFocus();
            return null;
        }

        // ── Phone: must be exactly 10 digits ─────────────────────────────────
        if (contact.isEmpty()) {
            AlertUtil.showWarning("Validation Error", "Contact number is required.");
            txtContact.requestFocus();
            return null;
        }
        if (!ValidationUtil.isValidPhone(contact)) {
            AlertUtil.showWarning("Invalid Phone Number",
                "Contact number must be exactly 10 digits (numbers only).\n" +
                "Entered: \"" + contact + "\"");
            txtContact.requestFocus();
            txtContact.selectAll();
            return null;
        }

        // ── Optional email format check ───────────────────────────────────────
        if (!ValidationUtil.isValidEmail(email)) {
            AlertUtil.showWarning("Invalid Email",
                "Please enter a valid email address, or leave it blank.");
            txtEmail.requestFocus();
            return null;
        }

        return new Customer(0, name, contact, email, address);
    }

    private void populateForm(Customer c) {
        txtName.setText(c.getName());
        txtContact.setText(c.getContact());
        txtEmail.setText(c.getEmail());
        txtAddress.setText(c.getAddress());
    }

    private void clearForm() {
        txtName.clear();
        txtContact.clear();
        txtEmail.clear();
        txtAddress.clear();
        txtContact.setStyle("");
        tblCustomers.getSelectionModel().clearSelection();
    }
}
