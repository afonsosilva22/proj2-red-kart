package com.example.desktop.controllers;

import com.example.desktop.models.Kart;
import com.example.desktop.models.KartTypePrice;
import com.example.desktop.services.KartService;
import com.example.desktop.services.KartTypePriceService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class EditKartController {

    @FXML private TextField txtKartNumber;
    @FXML private ComboBox<KartTypePrice> comboType;
    @FXML private TextField txtMileage;
    @FXML private TextField txtYear;
    @FXML private DatePicker dpLastService;
    @FXML private ComboBox<String> comboStatus;

    private final KartService kartService = new KartService();
    private final KartTypePriceService typeService = new KartTypePriceService();

    private Kart currentKart;

    @FXML
    public void initialize() {
        comboStatus.setItems(FXCollections.observableArrayList("available", "in_use", "maintenance", "scrapped"));

        try {
            List<KartTypePrice> types = typeService.getAllTypes();
            comboType.setItems(FXCollections.observableArrayList(types));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Called by KartController to pass in the selected row
    public void setKart(Kart kart) {
        this.currentKart = kart;

        txtKartNumber.setText(kart.getKartNumber());
        txtMileage.setText(String.valueOf(kart.getMileage()));
        txtYear.setText(String.valueOf(kart.getManufactureYear()));
        comboStatus.setValue(kart.getStatus());

        if (kart.getLastServiceDate() != null) {
            dpLastService.setValue(LocalDate.parse(kart.getLastServiceDate()));
        }

        // Auto-select the correct Type in the ComboBox
        if (kart.getType() != null) {
            for (KartTypePrice type : comboType.getItems()) {
                if (type.getType().equals(kart.getType().getType())) {
                    comboType.setValue(type);
                    break;
                }
            }
        }
    }

    @FXML
    private void saveChanges() {
        try {
            currentKart.setKartNumber(txtKartNumber.getText().trim());
            currentKart.setType(comboType.getValue());
            currentKart.setMileage(Integer.parseInt(txtMileage.getText().trim()));
            currentKart.setManufactureYear(Integer.parseInt(txtYear.getText().trim()));

            if (dpLastService.getValue() != null) {
                currentKart.setLastServiceDate(dpLastService.getValue().toString());
            } else {
                currentKart.setLastServiceDate(null);
            }

            currentKart.setStatus(comboStatus.getValue());

            // Push update to server
            kartService.update(currentKart.getId(), currentKart);
            closeWindow();

        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Mileage and Year must be numbers.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Network Error", "Failed to update the kart.");
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