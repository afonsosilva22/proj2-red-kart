package com.example.desktop.controllers;

import com.example.desktop.models.Equipment;
import com.example.desktop.services.EquipmentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AddEquipmentController {

    @FXML private ComboBox<String> comboType;
    @FXML private ComboBox<String> comboSize;
    @FXML private TextField txtBrand;
    @FXML private TextField txtColor;

    private final EquipmentService service = new EquipmentService();

    @FXML
    public void initialize() {
        // Enforce DB constraint explicit lowercase values
        comboType.setItems(FXCollections.observableArrayList("helmet", "gloves", "karting_suit"));

        // Enforce requested standard sizing categories
        comboSize.setItems(FXCollections.observableArrayList("XS", "S", "M", "L", "XL", "XXL"));
    }

    @FXML
    private void saveEquipment() {
        try {
            if (comboType.getValue() == null || comboSize.getValue() == null) {
                showAlert("Validation Error", "Please select both a Type and a Size.");
                return;
            }

            Equipment eq = new Equipment();
            eq.setType(comboType.getValue());
            eq.setSize(comboSize.getValue());
            eq.setBrand(txtBrand.getText().trim());
            eq.setColor(txtColor.getText().trim());

            // Automatically capture current date seamlessly in the background
            eq.setAcquisitionDate(LocalDate.now().toString());

            // Hardcoded status entry rule matching DB constraint defaults
            eq.setStatus("available");

            service.create(eq);
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not create equipment entry.");
        }
    }

    @FXML private void cancel() { closeWindow(); }
    private void closeWindow() { ((Stage) comboType.getScene().getWindow()).close(); }
    private void showAlert(String title, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR); a.setTitle(title); a.setContentText(content); a.showAndWait();
    }
}