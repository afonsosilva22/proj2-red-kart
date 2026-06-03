package com.example.desktop.controllers;

import com.example.desktop.models.BlacklistEntry;
import com.example.desktop.models.Customer;
import com.example.desktop.services.BlacklistEntryService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class AddBlacklistController {

    @FXML private Label lblCustomerName;
    @FXML private ComboBox<String> cmbReason;

    private final BlacklistEntryService blacklistService = new BlacklistEntryService();
    private Customer selectedCustomer;
    private boolean confirmed = false;

    private final Map<String, String> reasonMap = new LinkedHashMap<>();

    @FXML
    public void initialize() {
        reasonMap.put("Misconduct", "misconduct");
        reasonMap.put("Non-Payment", "non_payment");
        reasonMap.put("Dangerous Driving", "dangerous_driving");
        reasonMap.put("Fraud / Identity Theft", "fraud");
        reasonMap.put("Other Reason", "other");

        cmbReason.setItems(FXCollections.observableArrayList(reasonMap.keySet()));
    }

    public void setCustomer(Customer customer) {
        this.selectedCustomer = customer;
        lblCustomerName.setText("Customer: " + customer.getName());
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML
    private void confirm() {
        String selectedDisplay = cmbReason.getValue();

        if (selectedDisplay == null) {
            return;
        }

        try {
            String dbReasonValue = reasonMap.get(selectedDisplay);

            Customer customerStub = new Customer();
            customerStub.setId(selectedCustomer.getId());

            String today = LocalDate.now().toString();

            BlacklistEntry entry = new BlacklistEntry(dbReasonValue, today, customerStub);
            blacklistService.create(entry);

            confirmed = true;
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cmbReason.getScene().getWindow();
        stage.close();
    }
}