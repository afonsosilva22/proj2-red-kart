package com.example.desktop.controllers;

import com.example.desktop.models.Equipment;
import com.example.desktop.services.EquipmentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class EditEquipmentController {

    @FXML private ComboBox<String> comboType;
    @FXML private ComboBox<String> comboSize;
    @FXML private TextField txtBrand;
    @FXML private TextField txtColor;
    @FXML private DatePicker dpAcquisition;
    @FXML private ComboBox<String> comboStatus;

    private final EquipmentService service = new EquipmentService();
    private Equipment targetEquipment;

    @FXML
    public void initialize() {
        // Enforce database constraints explicitly
        comboType.setItems(FXCollections.observableArrayList("helmet", "gloves", "karting_suit"));
        comboSize.setItems(FXCollections.observableArrayList("XS", "S", "M", "L", "XL", "XXL"));

        // Exact items matching equipment_status_check constraint array
        comboStatus.setItems(FXCollections.observableArrayList("available", "in_use", "maintenance", "scrapped"));
    }

    public void setEquipment(Equipment eq) {
        this.targetEquipment = eq;

        comboType.setValue(eq.getType());
        comboSize.setValue(eq.getSize());
        txtBrand.setText(eq.getBrand());
        txtColor.setText(eq.getColor());
        comboStatus.setValue(eq.getStatus());

        if (eq.getAcquisitionDate() != null) {
            dpAcquisition.setValue(LocalDate.parse(eq.getAcquisitionDate()));
        }
    }

    @FXML
    private void updateEquipment() {
        try {
            if (comboType.getValue() == null || comboSize.getValue() == null || comboStatus.getValue() == null) {
                return;
            }

            targetEquipment.setType(comboType.getValue());
            targetEquipment.setSize(comboSize.getValue());
            targetEquipment.setBrand(txtBrand.getText().trim());
            targetEquipment.setColor(txtColor.getText().trim());
            targetEquipment.setStatus(comboStatus.getValue());

            // Keeps acquisition date protected and unmodified

            service.update(targetEquipment.getId(), targetEquipment);
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void cancel() { closeWindow(); }
    private void closeWindow() { ((Stage) comboType.getScene().getWindow()).close(); }
}