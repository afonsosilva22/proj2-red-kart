package com.example.desktop.controllers;

import com.example.desktop.models.Kart;
import com.example.desktop.services.KartService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class KartController {

    @FXML private TableView<Kart> table;
    @FXML private TableColumn<Kart, Integer> colId;
    @FXML private TableColumn<Kart, String> colKartNumber;
    @FXML private TableColumn<Kart, String> colType;
    @FXML private TableColumn<Kart, Integer> colMileage;
    @FXML private TableColumn<Kart, Integer> colManufactureYear;
    @FXML private TableColumn<Kart, String> colLastService;
    @FXML private TableColumn<Kart, String> colStatus;

    // Technical Detail Inspector Panel Bindings
    @FXML private Label lblKartNumber;
    @FXML private Label lblType;
    @FXML private Label lblMileage;
    @FXML private Label lblManufactureYear;
    @FXML private Label lblLastService;
    @FXML private Label lblStatus;

    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private final KartService service = new KartService();

    @FXML
    public void initialize() {
        // Map table columns to model property primitives
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colKartNumber.setCellValueFactory(new PropertyValueFactory<>("kartNumber"));
        colMileage.setCellValueFactory(new PropertyValueFactory<>("mileage"));
        colManufactureYear.setCellValueFactory(new PropertyValueFactory<>("manufactureYear"));
        colLastService.setCellValueFactory(new PropertyValueFactory<>("lastServiceDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Maps column to getKartTypeName() wrapper method in your model
        colType.setCellValueFactory(new PropertyValueFactory<>("kartTypeName"));

        // Row Selection Interceptor Update Logic Loop
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, kart) -> {
            if (kart != null) {
                showKartDetails(kart);
                btnEdit.setDisable(false);
                btnDelete.setDisable(false);
            } else {
                btnEdit.setDisable(true);
                btnDelete.setDisable(true);
            }
        });

        loadKarts();
    }

    private void showKartDetails(Kart kart) {
        lblKartNumber.setText("Kart Number: " + kart.getKartNumber());
        lblType.setText(String.format("Type Classification: %s (%s)", kart.getKartTypeName(), kart.getPricePerHourDisplay()));
        lblMileage.setText("Total Mileage: " + (kart.getMileage() != null ? kart.getMileage() + " km" : "0 km"));
        lblManufactureYear.setText("Manufacture Year: " + kart.getManufactureYear());
        lblLastService.setText("Last Serviced Date: " + (kart.getLastServiceDate() != null ? kart.getLastServiceDate() : "Never"));
        lblStatus.setText("Operational Status: " + (kart.getStatus() != null ? kart.getStatus().toUpperCase() : "N/A"));
    }

    @FXML
    public void loadKarts() {
        try {
            java.util.List<Kart> allKarts = service.getAllKarts();

            java.util.List<Kart> activeKarts = allKarts.stream()
                    .filter(kart -> "available".equalsIgnoreCase(kart.getStatus()) || "in_use".equalsIgnoreCase(kart.getStatus()) || "maintenance".equalsIgnoreCase(kart.getStatus()))
                    .collect(java.util.stream.Collectors.toList());

            table.setItems(FXCollections.observableArrayList(activeKarts));
            table.getSelectionModel().clearSelection();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addKart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/desktop/add-kart.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Provision New Kart Asset");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadKarts();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addKartType() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/desktop/add-kart-type.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Register New Kart Class");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editKart() {
        Kart selectedKart = table.getSelectionModel().getSelectedItem();
        if (selectedKart == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/desktop/edit-kart.fxml"));
            Parent root = loader.load();

            EditKartController controller = loader.getController();
            controller.setKart(selectedKart);

            Stage stage = new Stage();
            stage.setTitle("Modify Technical Specifications");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadKarts();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteKart() {
        Kart selectedKart = table.getSelectionModel().getSelectedItem();
        if (selectedKart == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Decommission Asset");
        alert.setHeaderText("Set Kart " + selectedKart.getKartNumber() + " to Out of Service?");
        alert.setContentText("This safely deactivates the kart from active bookings while leaving its records intact.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                selectedKart.setStatus("scrapped"); // Soft status deactivation logic
                service.update(selectedKart.getId(), selectedKart);
                loadKarts();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}