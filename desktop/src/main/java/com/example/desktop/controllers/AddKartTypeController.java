package com.example.desktop.controllers;

import com.example.desktop.models.KartTypePrice;
import com.example.desktop.services.KartTypePriceService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class AddKartTypeController {

    @FXML private TextField txtType;
    @FXML private TextField txtPrice;

    private final KartTypePriceService service = new KartTypePriceService();

    @FXML
    private void saveType() {
        try {
            KartTypePrice newType = new KartTypePrice();

            // Validate and set Type Name
            String typeName = txtType.getText().trim();
            if (typeName.isEmpty()) {
                showAlert("Validation Error", "Type name cannot be empty.");
                return;
            }
            newType.setType(typeName);

            // Validate and set Price
            BigDecimal price = new BigDecimal(txtPrice.getText().trim());
            newType.setPricePerHour(price);

            // Push to backend
            service.create(newType);
            closeWindow();

        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Price must be a valid decimal number (e.g., 15.50).");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Network Error", "Failed to save the new kart type.");
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtType.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}