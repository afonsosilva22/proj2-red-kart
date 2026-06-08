package com.example.desktop.controllers;

import com.example.desktop.models.Equipment;
import com.example.desktop.services.EquipmentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class EquipmentController {

    @FXML private TableView<Equipment> table;
    @FXML private TableColumn<Equipment, Integer> colId;
    @FXML private TableColumn<Equipment, String> colType;
    @FXML private TableColumn<Equipment, String> colSize;
    @FXML private TableColumn<Equipment, String> colBrand;
    @FXML private TableColumn<Equipment, String> colColor;
    @FXML private TableColumn<Equipment, String> colAcquisition;
    @FXML private TableColumn<Equipment, String> colStatus;

    @FXML private Label lblType;
    @FXML private Label lblSize;
    @FXML private Label lblBrand;
    @FXML private Label lblColor;
    @FXML private Label lblAcquisition;
    @FXML private Label lblStatus;

    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private final EquipmentService service = new EquipmentService();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colAcquisition.setCellValueFactory(new PropertyValueFactory<>("acquisitionDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, eq) -> {
            if (eq != null) {
                showEquipmentDetails(eq);
                btnEdit.setDisable(false);
                btnDelete.setDisable(false);
            } else {
                btnEdit.setDisable(true);
                btnDelete.setDisable(true);
            }
        });

        loadEquipments();
    }

    @FXML
    public void loadEquipments() {
        try {
            java.util.List<Equipment> allEquipment = service.getAllEquipment();

            // Updated stream filter: only includes available, in_use, or maintenance
            java.util.List<Equipment> activeEquipment = allEquipment.stream()
                    .filter(eq -> "available".equalsIgnoreCase(eq.getStatus()) ||
                            "in_use".equalsIgnoreCase(eq.getStatus()) ||
                            "maintenance".equalsIgnoreCase(eq.getStatus()))
                    .collect(java.util.stream.Collectors.toList());

            table.setItems(FXCollections.observableArrayList(activeEquipment));
            table.getSelectionModel().clearSelection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showEquipmentDetails(Equipment eq) {
        lblType.setText("Equipment Type: " + eq.getType());
        lblSize.setText("Size Classification: " + eq.getSize());
        lblBrand.setText("Manufacturer Brand: " + (eq.getBrand() != null ? eq.getBrand() : "N/A"));
        lblColor.setText("Color Trim: " + (eq.getColor() != null ? eq.getColor() : "N/A"));
        lblAcquisition.setText("Acquisition Date: " + eq.getAcquisitionDate());
        lblStatus.setText("Operational Status: " + eq.getStatus().toUpperCase());
    }

    @FXML
    private void addEquipment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/desktop/add-equipment.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Register New Track Gear");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadEquipments();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editEquipment() {
        Equipment selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/desktop/edit-equipment.fxml"));
            Parent root = loader.load();
            EditEquipmentController controller = loader.getController();
            controller.setEquipment(selected);
            Stage stage = new Stage();
            stage.setTitle("Modify Gear Condition Status");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadEquipments();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteEquipment() {
        Equipment selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Display a clean confirmation dialog before processing the soft-delete
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Scrap Equipment Item #" + selected.getId());
        confirmation.setContentText("Are you sure you want to mark this item as scrapped? It will be removed from your active inventory view.");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                // Mutate the status field to match database constraint expectations
                selected.setStatus("scrapped");

                // Push change out through your network infrastructure layer
                service.update(selected.getId(), selected);

                // Refresh local view dynamically
                loadEquipments();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Failed to update equipment status on the backend server.");
                alert.showAndWait();
            }
        }
    }
}