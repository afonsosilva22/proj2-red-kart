package com.example.desktop.controllers;

import com.example.desktop.models.Customer;
import com.example.desktop.models.PostalCode;
import com.example.desktop.services.CustomerService;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class AddCustomerController {

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

    @FXML
    private void saveCustomer() {
        try {
            Customer customer = new Customer();

            customer.setName(nameField.getText());
            customer.setNif(nifField.getText());
            customer.setEmail(emailField.getText());
            customer.setPhone(phoneField.getText());
            customer.setDoorNumber(doorNumberField.getText());
            customer.setStreet(streetField.getText());

            String postalCode = postalCodeField.getText();
            String locality = localityField.getText();
            PostalCode pc = new PostalCode(postalCode, locality);
            customer.setPostalCode(pc);

            customer.setBirthDate(birthdatePicker.getValue().toString());

            String rawName = nameField.getText();
            if (rawName != null && !rawName.trim().isEmpty()) {
                String cleanUsername = rawName.replaceAll("\\s+", "").toLowerCase();
                customer.setUsername(cleanUsername);
            } else {
                customer.setUsername("user_" + java.util.concurrent.ThreadLocalRandom.current().nextInt(1000, 10000));
            }
            int randomPin = java.util.concurrent.ThreadLocalRandom.current().nextInt(1000, 10000);
            customer.setPassword(String.valueOf(randomPin));
            customer.setIsMember(false);
            customer.setNumViolations(0);
            customer.setRegistrationDate(String.valueOf(LocalDate.now()));
            customer.setStatus("active");

            Customer createdCustomer = customerService.create(customer);

            System.out.println("Customer created: " + createdCustomer.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}