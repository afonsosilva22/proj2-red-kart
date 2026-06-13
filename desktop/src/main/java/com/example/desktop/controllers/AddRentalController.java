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
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AddRentalController {

    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<Employee> employeeComboBox;
    @FXML private ComboBox<String> typeComboBox;

    @FXML private DatePicker rentalDatePicker;

    @FXML private ComboBox<String> startHourCombo;
    @FXML private ComboBox<String> startMinuteCombo;
    @FXML private ComboBox<String> endHourCombo;
    @FXML private ComboBox<String> endMinuteCombo;

    @FXML private TextField basePriceField;
    @FXML private TextField discountField;

    private final RentalService rentalService = new RentalService();
    private final CustomerService customerService = new CustomerService();
    private final EmployeeService employeeService = new EmployeeService();

    @FXML
    public void initialize() {
        typeComboBox.setItems(FXCollections.observableArrayList("track", "kart"));

        ObservableList<String> hours = FXCollections.observableArrayList();
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d", i));
        }

        ObservableList<String> minutes = FXCollections.observableArrayList();
        for (int i = 0; i < 60; i += 5) {
            minutes.add(String.format("%02d", i));
        }

        startHourCombo.setItems(hours);
        endHourCombo.setItems(hours);
        startMinuteCombo.setItems(minutes);
        endMinuteCombo.setItems(minutes);

        startHourCombo.setValue("12");
        startMinuteCombo.setValue("00");
        endHourCombo.setValue("13");
        endMinuteCombo.setValue("00");

        try {
            List<Customer> activeCustomers = customerService.getAllCustomers().stream()
                    .filter(c -> "active".equalsIgnoreCase(c.getStatus()))
                    .collect(Collectors.toList());
            customerComboBox.setItems(FXCollections.observableArrayList(activeCustomers));

            List<Employee> activeEmployees = employeeService.getAllEmployees().stream()
                    .filter(e -> "active".equalsIgnoreCase(e.getStatus()))
                    .collect(Collectors.toList());
            employeeComboBox.setItems(FXCollections.observableArrayList(activeEmployees));
        } catch (Exception e) {
            e.printStackTrace();
        }

        customerComboBox.setConverter(new StringConverter<Customer>() {
            @Override public String toString(Customer c) { return c == null ? "" : c.getName() + " (ID: " + c.getId() + ")"; }
            @Override public Customer fromString(String s) { return null; }
        });

        employeeComboBox.setConverter(new StringConverter<Employee>() {
            @Override public String toString(Employee e) { return e == null ? "" : e.getName() + " (ID: " + e.getId() + ")"; }
            @Override public Employee fromString(String s) { return null; }
        });
    }

    @FXML
    private void saveRental() {
        try {
            Customer selectedCustomer = customerComboBox.getSelectionModel().getSelectedItem();
            Employee selectedEmployee = employeeComboBox.getSelectionModel().getSelectedItem();
            String selectedType = typeComboBox.getSelectionModel().getSelectedItem();

            LocalDate rentalDate = rentalDatePicker.getValue();

            String startH = startHourCombo.getValue();
            String startM = startMinuteCombo.getValue();
            String endH = endHourCombo.getValue();
            String endM = endMinuteCombo.getValue();

            if (selectedCustomer == null || selectedEmployee == null || selectedType == null) {
                showErrorAlert("Validation Error", "Please ensure all dropdown selections are filled.");
                return;
            }
            if (rentalDate == null || startH == null || startM == null || endH == null || endM == null) {
                showErrorAlert("Validation Error", "Please specify a Date and complete Time windows.");
                return;
            }

            // Build ISO-8601 strings
            String startTimestamp = rentalDate.toString() + "T" + startH + ":" + startM + ":00Z";
            String endTimestamp = rentalDate.toString() + "T" + endH + ":" + endM + ":00Z";

            // Parse to Instant for comparison
            Instant newStart = Instant.parse(startTimestamp);
            Instant newEnd = Instant.parse(endTimestamp);

            if (!newStart.isBefore(newEnd)) {
                showErrorAlert("Validation Error", "Planned Start Time must be earlier than the End Time.");
                return;
            }

            // ─── NEW: OVERLAP & EXCLUSIVITY VALIDATION ───
            List<Rental> allRentals = rentalService.getAllRentals();
            for (Rental existing : allRentals) {
                // Ignore cancelled or completed sessions
                if (existing.getStatus() != null &&
                        (existing.getStatus().equalsIgnoreCase("cancelled") || existing.getStatus().equalsIgnoreCase("finished"))) {
                    continue;
                }

                if (existing.getPlannedStartDatetime() == null || existing.getPlannedEndDatetime() == null) {
                    continue;
                }

                Instant extStart = Instant.parse(existing.getPlannedStartDatetime());
                Instant extEnd = Instant.parse(existing.getPlannedEndDatetime());

                // Overlap formula: (Start_A < End_B) AND (Start_B < End_A)
                if (newStart.isBefore(extEnd) && extStart.isBefore(newEnd)) {
                    // Block booking if either the new rental or the existing overlapping rental is a 'track'
                    if ("track".equalsIgnoreCase(selectedType) || "track".equalsIgnoreCase(existing.getType())) {
                        showErrorAlert(
                                "Scheduling Conflict",
                                "The selected time window overlaps with an existing " + existing.getType().toUpperCase() +
                                        " session (ID: " + existing.getId() + "). Exclusive track rules apply."
                        );
                        return; // Halt execution and don't save
                    }
                }
            }
            // ─────────────────────────────────────────────

            Rental rental = new Rental();
            rental.setCustomer(selectedCustomer);
            rental.setEmployee(selectedEmployee);
            rental.setType(selectedType);
            rental.setStatus("scheduled");
            rental.setPlannedStartDatetime(startTimestamp);
            rental.setPlannedEndDatetime(endTimestamp);

            rental.setBasePrice(new BigDecimal(basePriceField.getText().trim()));
            String discountRaw = discountField.getText().trim();

            if (!discountRaw.isEmpty()) {
                BigDecimal discountPercentage = new BigDecimal(discountRaw);
                BigDecimal decimalValue = discountPercentage.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                rental.setDiscount(decimalValue);
            } else {
                rental.setDiscount(BigDecimal.ZERO);
            }

            rentalService.create(rental);

            Stage stage = (Stage) basePriceField.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showErrorAlert("Input Error", "Please ensure Price and Discount fields contain valid numbers.");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("System Error", "Failed to compile rental record: " + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}