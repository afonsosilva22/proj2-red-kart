package com.example.desktop.controllers;

import com.example.desktop.models.Payment;
import com.example.desktop.models.Rental;
import com.example.desktop.services.PaymentService;
import com.example.desktop.services.RentalService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

public class RegisterPaymentController {

    @FXML private Label lblRentalId;
    @FXML private TextField txtAmountPaid;
    @FXML private TextField txtIvaRate;
    @FXML private ComboBox<String> comboMethod;

    private final PaymentService paymentService = new PaymentService();
    private final RentalService rentalService = new RentalService();

    private Rental currentRental;

    @FXML
    public void initialize() {
        comboMethod.setItems(FXCollections.observableArrayList("Cash", "Credit Card", "Debit Card", "Transfer", "MB Way"));
    }

    public void setRentalContext(Rental rental) {
        this.currentRental = rental;
        this.lblRentalId.setText(String.valueOf(rental.getId()));

        BigDecimal finalPrice = rental.getBasePrice();
        if (finalPrice != null && rental.getDiscount() != null && rental.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            finalPrice = finalPrice.multiply(BigDecimal.ONE.subtract(rental.getDiscount()));
        }

        if (finalPrice != null) {
            txtAmountPaid.setText(finalPrice.setScale(2, RoundingMode.HALF_UP).toString());
        }
    }

    @FXML
    private void processPayment() {
        try {
            String uiMethod = comboMethod.getValue();
            String amountRaw = txtAmountPaid.getText().trim();
            String ivaRaw = txtIvaRate.getText().trim();

            if (uiMethod == null || amountRaw.isEmpty() || ivaRaw.isEmpty()) {
                showError("Validation Error", "Please complete all transaction parameters.");
                return;
            }

            String dbMappedMethod;
            switch (uiMethod) {
                case "Cash":
                    dbMappedMethod = "cash";
                    break;
                case "Credit Card":
                    dbMappedMethod = "credit_card";
                    break;
                case "Debit Card":
                    dbMappedMethod = "debit_card";
                    break;
                case "Transfer":
                    dbMappedMethod = "transfer";
                    break;
                case "MB Way":
                    dbMappedMethod = "mbway";
                    break;
                default:
                    dbMappedMethod = uiMethod.toLowerCase().replace(" ", "_");
                    break;
            }

            Payment payment = new Payment();
            payment.setRental(currentRental);
            payment.setAmountPaid(new BigDecimal(amountRaw));
            payment.setIvaRate(new BigDecimal(ivaRaw));
            payment.setPaymentMethod(dbMappedMethod);
            payment.setPaymentDate(Instant.now().toString());

            paymentService.create(payment);

            currentRental.setStatus("fully_payed");
            rentalService.update(currentRental.getId(), currentRental);

            closeWindow();

        } catch (NumberFormatException e) {
            showError("Input Error", "Please verify numerical syntax layouts inside payment amounts or tax attributes.");
        } catch (Exception e) {
            e.printStackTrace();
            showError("System Failure", "Failed to clear ledger entry transaction: " + e.getMessage());
        }
    }

    @FXML private void closeWindow() { ((Stage) txtAmountPaid.getScene().getWindow()).close(); }

    private void showError(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
}