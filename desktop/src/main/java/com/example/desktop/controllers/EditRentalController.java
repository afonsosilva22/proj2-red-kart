package com.example.desktop.controllers;

import com.example.desktop.models.Customer;
import com.example.desktop.models.Employee;
import com.example.desktop.models.Rental;
import com.example.desktop.services.CustomerService;
import com.example.desktop.services.EmployeeService;
import com.example.desktop.services.RentalService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class EditRentalController {

    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<Employee> employeeComboBox;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> statusComboBox;

    @FXML private DatePicker rentalDatePicker;
    @FXML private ComboBox<String> startHourCombo;
    @FXML private ComboBox<String> startMinuteCombo;
    @FXML private ComboBox<String> endHourCombo;
    @FXML private ComboBox<String> endMinuteCombo;

    @FXML private TextField basePriceField;
    @FXML private TextField discountField;
    @FXML private TextField txtComplaint;

    private final RentalService rentalService = new RentalService();
    private final CustomerService customerService = new CustomerService();
    private final EmployeeService employeeService = new EmployeeService();

    private Rental targetRental;

    @FXML
    public void initialize() {
        typeComboBox.setItems(FXCollections.observableArrayList("track", "kart"));

        // UPDATED: Restructured allowed lifecycle states exactly to your specifications
        statusComboBox.setItems(FXCollections.observableArrayList(
                "scheduled", "fully_payed", "cancelled", "finished"
        ));

        // Generate Time Drops
        ObservableList<String> hours = FXCollections.observableArrayList();
        for (int i = 0; i < 24; i++) hours.add(String.format("%02d", i));

        ObservableList<String> minutes = FXCollections.observableArrayList();
        for (int i = 0; i < 60; i += 5) minutes.add(String.format("%02d", i));

        startHourCombo.setItems(hours);
        endHourCombo.setItems(hours);
        startMinuteCombo.setItems(minutes);
        endMinuteCombo.setItems(minutes);

        setupDropdownConverters();
        loadDatabaseRelations();
    }

    public void setRental(Rental rental) {
        this.targetRental = rental;

        // 1. Map Core Fields
        typeComboBox.setValue(rental.getType());
        statusComboBox.setValue(rental.getStatus());
        basePriceField.setText(rental.getBasePrice() != null ? rental.getBasePrice().toString() : "0.00");
        txtComplaint.setText(rental.getComplaint() != null ? rental.getComplaint() : "");

        // 2. Map Discount back to user-friendly whole number (e.g., 0.40 -> 40)
        if (rental.getDiscount() != null) {
            BigDecimal wholePercent = rental.getDiscount().multiply(new BigDecimal("100"));
            discountField.setText(String.valueOf(wholePercent.intValue()));
        } else {
            discountField.setText("0");
        }

        // 3. Select existing customer & employee relationships
        if (rental.getCustomer() != null) {
            customerComboBox.getItems().stream()
                    .filter(c -> c.getId().equals(rental.getCustomer().getId()))
                    .findFirst().ifPresent(c -> customerComboBox.setValue(c));
        }
        if (rental.getEmployee() != null) {
            employeeComboBox.getItems().stream()
                    .filter(e -> e.getId().equals(rental.getEmployee().getId()))
                    .findFirst().ifPresent(e -> employeeComboBox.setValue(e));
        }

        // 4. Parse Dates & Times from ISO Timestamps (YYYY-MM-DDTHH:MM:SSZ)
        parseAndSetTimestamps(rental.getPlannedStartDatetime(), rental.getPlannedEndDatetime());
    }

    private void parseAndSetTimestamps(String startIso, String endIso) {
        try {
            if (startIso != null && startIso.contains("T")) {
                String[] startParts = startIso.split("T");
                rentalDatePicker.setValue(LocalDate.parse(startParts[0]));

                String[] startTime = startParts[1].split(":");
                startHourCombo.setValue(startTime[0]);
                startMinuteCombo.setValue(startTime[1]);
            }
            if (endIso != null && endIso.contains("T")) {
                String[] endParts = endIso.split("T");
                String[] endTime = endParts[1].split(":");
                endHourCombo.setValue(endTime[0]);
                endMinuteCombo.setValue(endTime[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateRental() {
        try {
            LocalDate rentalDate = rentalDatePicker.getValue();
            String startH = startHourCombo.getValue();
            String startM = startMinuteCombo.getValue();
            String endH = endHourCombo.getValue();
            String endM = endMinuteCombo.getValue();

            if (rentalDate == null || startH == null || startM == null || endH == null || endM == null) {
                showErrorAlert("Validation Error", "Please ensure scheduling windows are filled.");
                return;
            }

            // Assign modified fields to target payload object
            targetRental.setCustomer(customerComboBox.getValue());
            targetRental.setEmployee(employeeComboBox.getValue());
            targetRental.setType(typeComboBox.getValue());
            targetRental.setStatus(statusComboBox.getValue());
            targetRental.setComplaint(txtComplaint.getText().trim().isEmpty() ? null : txtComplaint.getText().trim());

            // Compile unified timestamp strings
            targetRental.setPlannedStartDatetime(rentalDate.toString() + "T" + startH + ":" + startM + ":00Z");
            targetRental.setPlannedEndDatetime(rentalDate.toString() + "T" + endH + ":" + endM + ":00Z");

            // Recalculate whole-number percentage back to DB decimal format (e.g. 40 -> 0.40)
            targetRental.setBasePrice(new BigDecimal(basePriceField.getText().trim()));
            String discountRaw = discountField.getText().trim();
            if (!discountRaw.isEmpty()) {
                BigDecimal decimalValue = new BigDecimal(discountRaw).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                targetRental.setDiscount(decimalValue);
            } else {
                targetRental.setDiscount(BigDecimal.ZERO);
            }

            // Fire updates out to backend database
            rentalService.update(targetRental.getId(), targetRental);

            // Close stage frame view
            ((Stage) basePriceField.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            showErrorAlert("Input Error", "Please check pricing or layout numerical input values.");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("System Error", "Failed to apply modifications: " + e.getMessage());
        }
    }

    @FXML private void cancel() { ((Stage) basePriceField.getScene().getWindow()).close(); }

    private void loadDatabaseRelations() {
        try {
            customerComboBox.setItems(FXCollections.observableArrayList(customerService.getAllCustomers()));
            employeeComboBox.setItems(FXCollections.observableArrayList(employeeService.getAllEmployees()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupDropdownConverters() {
        customerComboBox.setConverter(new StringConverter<Customer>() {
            @Override public String toString(Customer c) { return c == null ? "" : c.getName() + " (ID: " + c.getId() + ")"; }
            @Override public Customer fromString(String s) { return null; }
        });
        employeeComboBox.setConverter(new StringConverter<Employee>() {
            @Override public String toString(Employee e) { return e == null ? "" : e.getName() + " (ID: " + e.getId() + ")"; }
            @Override public Employee fromString(String s) { return null; }
        });
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}