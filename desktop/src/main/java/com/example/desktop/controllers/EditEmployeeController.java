package com.example.desktop.controllers;

import com.example.desktop.models.Employee;
import com.example.desktop.models.PostalCode;
import com.example.desktop.services.EmployeeService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class EditEmployeeController {

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

    private final EmployeeService employeeService = new EmployeeService();
    private Employee currentEmployee;

    @FXML
    public void initialize() {
        typeComboBox.setItems(FXCollections.observableArrayList("manager", "mechanic", "receptionist"));
    }

    public void setEmployee(Employee employee) {
        if (employee == null) return;
        this.currentEmployee = employee;

        nameField.setText(employee.getName() != null ? employee.getName() : "");
        nifField.setText(employee.getNif() != null ? employee.getNif() : "");
        emailField.setText(employee.getEmail() != null ? employee.getEmail() : "");
        phoneField.setText(employee.getPhone() != null ? employee.getPhone() : "");
        typeComboBox.setValue(employee.getType());
        streetField.setText(employee.getStreet() != null ? employee.getStreet() : "");
        doorNumberField.setText(employee.getDoorNumber() != null ? employee.getDoorNumber() : "");

        if (employee.getPostalCode() != null) {
            postalCodeField.setText(employee.getPostalCode().getPostalCode() != null ? employee.getPostalCode().getPostalCode() : "");
            localityField.setText(employee.getPostalCode().getLocality() != null ? employee.getPostalCode().getLocality() : "");
        } else {
            postalCodeField.setText(employee.getPostalCodeValue() != null ? employee.getPostalCodeValue() : "");
            localityField.setText("");
        }

        if (employee.getBirthdate() != null && !employee.getBirthdate().isEmpty()) {
            try {
                birthdatePicker.setValue(LocalDate.parse(employee.getBirthdate()));
            } catch (Exception e) {
                birthdatePicker.setValue(null);
            }
        }
    }

    @FXML
    private void updateEmployee() {
        if (currentEmployee == null) return;

        try {
            currentEmployee.setName(getSafeText(nameField));
            currentEmployee.setNif(getSafeText(nifField));
            currentEmployee.setEmail(getSafeText(emailField));
            currentEmployee.setPhone(getSafeText(phoneField));
            currentEmployee.setType(typeComboBox.getValue());
            currentEmployee.setStreet(getSafeText(streetField));
            currentEmployee.setDoorNumber(getSafeText(doorNumberField));

            PostalCode pc = new PostalCode(getSafeText(postalCodeField), getSafeText(localityField));
            currentEmployee.setPostalCode(pc);

            if (birthdatePicker.getValue() != null) {
                currentEmployee.setBirthdate(birthdatePicker.getValue().toString());
            } else {
                currentEmployee.setBirthdate(null);
            }

            // Sync down to back-end API
            employeeService.update(currentEmployee.getId(), currentEmployee);
            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSafeText(TextField field) {
        if (field == null || field.getText() == null) {
            return "";
        }
        return field.getText().trim();
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}