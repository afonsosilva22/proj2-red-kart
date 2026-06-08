package com.example.desktop.controllers;

import com.example.desktop.models.Employee;
import com.example.desktop.models.PostalCode;
import com.example.desktop.services.EmployeeService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public class AddEmployeeController {

    @FXML private TextField nameField;
    @FXML private TextField nifField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private DatePicker birthdatePicker;
    @FXML private ComboBox<String> typeComboBox;

    @FXML private TextField streetField;
    @FXML private TextField doorNumberField;
    @FXML private TextField postalCodeField;
    @FXML private TextField localityField;

    private final EmployeeService service = new EmployeeService();

    @FXML
    public void initialize() {
        // Standard karting center operational employment matrix assignments
        typeComboBox.setItems(FXCollections.observableArrayList("manager", "mechanic", "receptionist"));
    }

    @FXML
    private void saveEmployee() {
        try {
            String rawName = nameField.getText().trim();
            String selectedType = typeComboBox.getSelectionModel().getSelectedItem();

            if (rawName.isEmpty() || selectedType == null) {
                showErrorAlert("Validation Error", "Name and Role Type are required fields.");
                return;
            }

            Employee emp = new Employee();
            emp.setName(rawName);
            emp.setNif(nifField.getText().trim());
            emp.setEmail(emailField.getText().trim());
            emp.setPhone(phoneField.getText().trim());
            emp.setType(selectedType);
            emp.setStatus("active");

            if (birthdatePicker.getValue() != null) {
                emp.setBirthdate(birthdatePicker.getValue().toString());
            }

            // Bind Address Details Structure Component Blocks
            emp.setStreet(streetField.getText().trim());
            emp.setDoorNumber(doorNumberField.getText().trim());

            PostalCode pc = new PostalCode(postalCodeField.getText().trim(), localityField.getText().trim());
            emp.setPostalCode(pc);

            // Generate clean workspace account credentials
            String cleanUsername = rawName.replaceAll("\\s+", "").toLowerCase();
            emp.setUsername(cleanUsername.isEmpty() ? "emp_" + ThreadLocalRandom.current().nextInt(1000, 9999) : cleanUsername);
            emp.setPassword(String.valueOf(ThreadLocalRandom.current().nextInt(1000, 9999)));

            // Save via Network Endpoint
            service.create(emp);

            // Close dialog prompt modal window
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("System Connection Failure", "Failed to preserve record info: " + e.getMessage());
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