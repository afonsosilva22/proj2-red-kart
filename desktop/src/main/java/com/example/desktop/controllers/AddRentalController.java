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
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AddRentalController {

    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<Employee> employeeComboBox;
    @FXML private ComboBox<String> typeComboBox;

    // Time Dropdown Adjustments
    @FXML private DatePicker startPicker;
    @FXML private ComboBox<String> startHourCombo;
    @FXML private ComboBox<String> startMinuteCombo;

    @FXML private DatePicker endPicker;
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

        // 1. Generate Hour and Minute lists
        ObservableList<String> hours = FXCollections.observableArrayList();
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d", i));
        }

        ObservableList<String> minutes = FXCollections.observableArrayList();
        for (int i = 0; i < 60; i += 5) { // 5-minute heat scheduling steps
            minutes.add(String.format("%02d", i));
        }

        startHourCombo.setItems(hours);
        endHourCombo.setItems(hours);
        startMinuteCombo.setItems(minutes);
        endMinuteCombo.setItems(minutes);

        // Pre-select default times to save clicks (Optional)
        startHourCombo.setValue("12");
        startMinuteCombo.setValue("00");
        endHourCombo.setValue("13");
        endMinuteCombo.setValue("00");

        // 2. Fetch Active Customers & Employees
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

        // 3. Set Up Converters for display naming conventions
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

            LocalDate startDate = startPicker.getValue();
            String startH = startHourCombo.getValue();
            String startM = startMinuteCombo.getValue();

            LocalDate endDate = endPicker.getValue();
            String endH = endHourCombo.getValue();
            String endM = endMinuteCombo.getValue();

            // Validation Checks
            if (selectedCustomer == null || selectedEmployee == null || selectedType == null) {
                showErrorAlert("Validation Error", "Please ensure all dropdown selections are filled.");
                return;
            }
            if (startDate == null || startH == null || startM == null || endDate == null || endH == null || endM == null) {
                showErrorAlert("Validation Error", "Please specify complete Date and Time windows.");
                return;
            }

            Rental rental = new Rental();
            rental.setCustomer(selectedCustomer);
            rental.setEmployee(selectedEmployee);
            rental.setType(selectedType);
            rental.setStatus("scheduled");

            // Assembly of precise ISO-8601 Instant Format: YYYY-MM-DDTHH:MM:SSZ
            String startTimestamp = startDate.toString() + "T" + startH + ":" + startM + ":00Z";
            String endTimestamp = endDate.toString() + "T" + endH + ":" + endM + ":00Z";

            rental.setPlannedStartDatetime(startTimestamp);
            rental.setPlannedEndDatetime(endTimestamp);

            // Financial Parsing
            rental.setBasePrice(new BigDecimal(basePriceField.getText().trim()));
            String discountRaw = discountField.getText().trim();
            rental.setDiscount(!discountRaw.isEmpty() ? new BigDecimal(discountRaw) : BigDecimal.ZERO);

            rentalService.create(rental);

            // Close Modal View Container Window
            Stage stage = (Stage) basePriceField.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showErrorAlert("Input Error", "Please check pricing or layout numerical input values.");
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