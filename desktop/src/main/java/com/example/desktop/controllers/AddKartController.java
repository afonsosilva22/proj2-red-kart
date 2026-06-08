package com.example.desktop.controllers;

import com.example.desktop.models.Kart;
import com.example.desktop.models.KartTypePrice;
import com.example.desktop.services.KartService;
import com.example.desktop.services.KartTypePriceService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class AddKartController {

    @FXML private TextField txtKartNumber;
    @FXML private ComboBox<KartTypePrice> comboType;
    @FXML private TextField txtMileage;
    @FXML private TextField txtYear;
    @FXML private DatePicker dpLastService;

    private final KartService kartService = new KartService();
    private final KartTypePriceService typeService = new KartTypePriceService();

    @FXML
    public void initialize() {
        // Fetch and populate Kart Types from the backend
        try {
            List<KartTypePrice> types = typeService.getAllTypes();
            comboType.setItems(FXCollections.observableArrayList(types));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load kart types from server.");
        }
    }

    @FXML
    private void saveKart() {
        try {
            Kart newKart = new Kart();
            newKart.setKartNumber(txtKartNumber.getText().trim());
            newKart.setType(comboType.getValue());
            newKart.setMileage(Integer.parseInt(txtMileage.getText().trim()));
            newKart.setManufactureYear(Integer.parseInt(txtYear.getText().trim()));

            if (dpLastService.getValue() != null) {
                newKart.setLastServiceDate(dpLastService.getValue().toString()); // Format: YYYY-MM-DD
            }

            newKart.setStatus("available");

            // Save via network
            kartService.create(newKart);
            closeWindow();

        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Mileage and Year must be valid numbers.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Network Error", "Failed to save the new kart.");
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtKartNumber.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}