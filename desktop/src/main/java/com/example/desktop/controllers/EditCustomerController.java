package com.example.desktop.controllers;

import com.example.desktop.models.Customer;
import com.example.desktop.models.PostalCode;
import com.example.desktop.services.CustomerService;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class EditCustomerController {

    @FXML private TextField nameField;
    @FXML private TextField nifField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField doorNumberField;
    @FXML private TextField streetField;
    @FXML private TextField postalCodeField;
    @FXML private TextField localityField;
    @FXML private DatePicker birthdatePicker;

    private final CustomerService customerService = new CustomerService();
    private Customer currentCustomer;

    public void setCustomer(Customer customer) {
        if (customer == null) return;

        this.currentCustomer = customer;

        nameField.setText(customer.getName());
        nifField.setText(customer.getNif());
        emailField.setText(customer.getEmail());
        phoneField.setText(customer.getPhone());
        streetField.setText(customer.getStreet());
        doorNumberField.setText(customer.getDoorNumber());

        if (customer.getPostalCode() != null) {
            postalCodeField.setText(customer.getPostalCode().getPostalCode());
            localityField.setText(customer.getPostalCode().getLocality());
        } else {
            postalCodeField.setText(customer.getPostalCodeValue() != null ? customer.getPostalCodeValue() : "");
            localityField.setText(customer.getLocality() != null ? customer.getLocality() : "");
        }

        if (customer.getBirthDate() != null && !customer.getBirthDate().isEmpty()) {
            birthdatePicker.setValue(LocalDate.parse(customer.getBirthDate()));
        }
    }

    @FXML
    private void updateCustomer() {
        if (currentCustomer == null) return;

        try {
            currentCustomer.setName(nameField.getText());
            currentCustomer.setNif(nifField.getText());
            currentCustomer.setEmail(emailField.getText());
            currentCustomer.setPhone(phoneField.getText());
            currentCustomer.setStreet(streetField.getText());
            currentCustomer.setDoorNumber(doorNumberField.getText());

            String postalCodeStr = postalCodeField.getText();
            String localityStr = localityField.getText();
            PostalCode pc = new PostalCode(postalCodeStr, localityStr);
            currentCustomer.setPostalCode(pc);

            if (birthdatePicker.getValue() != null) {
                currentCustomer.setBirthDate(birthdatePicker.getValue().toString());
            }

            customerService.update(currentCustomer.getId(), currentCustomer);

            System.out.println("Customer updated: " + currentCustomer.getName());

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
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}