package com.example.desktop.controllers;

import com.example.desktop.models.Maintenance;
import com.example.desktop.services.MaintenanceService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class MaintenanceController {

    @FXML private TableView<Maintenance> table;
    @FXML private TableColumn<Maintenance, Integer> colId;
    @FXML private TableColumn<Maintenance, String> colType;
    @FXML private TableColumn<Maintenance, String> colTarget;
    @FXML private TableColumn<Maintenance, String> colPriority;
    @FXML private TableColumn<Maintenance, String> colStatus;
    @FXML private TableColumn<Maintenance, String> colOpenDate;

    @FXML private Label lblDescription;
    @FXML private Label lblEmployee;
    @FXML private Label lblCompletion;

    @FXML private Button btnEdit;

    private final MaintenanceService service = new MaintenanceService();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colOpenDate.setCellValueFactory(new PropertyValueFactory<>("openDate"));

        // Dynamically resolve target based on type
        colTarget.setCellValueFactory(cellData -> {
            Maintenance m = cellData.getValue();
            if ("track".equals(m.getType()) && m.getTrack() != null) {
                return new SimpleStringProperty(m.getTrack().getName());
            } else if ("kart".equals(m.getType()) && m.getKart() != null) {
                return new SimpleStringProperty("Kart ID: " + m.getKart().getId());
            }
            return new SimpleStringProperty("Unassigned");
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, m) -> {
            if (m != null) {
                showDetails(m);
                btnEdit.setDisable(false);
            } else {
                btnEdit.setDisable(true);
            }
        });

        loadMaintenances();
    }

    @FXML
    public void loadMaintenances() {
        try {
            List<Maintenance> allMaintenances = service.getAllMaintenances();

            // Filter out finished jobs from the active queue
            List<Maintenance> activeQueue = allMaintenances.stream()
                    .filter(m -> !"completed".equalsIgnoreCase(m.getStatus()) && !"unrepairable".equalsIgnoreCase(m.getStatus()))
                    .collect(Collectors.toList());

            table.setItems(FXCollections.observableArrayList(activeQueue));
            table.getSelectionModel().clearSelection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDetails(Maintenance m) {
        lblDescription.setText("Description: " + (m.getDescription() != null ? m.getDescription() : "None provided."));
        lblEmployee.setText("Assigned Tech: " + (m.getEmployee() != null ? "Employee ID: " + m.getEmployee().getId() : "Unassigned"));
        lblCompletion.setText("Completion Date: " + (m.getCompletionDate() != null ? m.getCompletionDate() : "Pending"));
    }

    @FXML
    private void addMaintenance() {
        openDialog("/com/example/desktop/add-maintenance.fxml", "Open Maintenance Ticket");
    }

    @FXML
    private void editMaintenance() {
        Maintenance selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/desktop/edit-maintenance.fxml"));
            Parent root = loader.load();
            EditMaintenanceController controller = loader.getController();
            controller.setMaintenance(selected);
            Stage stage = new Stage();
            stage.setTitle("Update Ticket Status");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadMaintenances();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDialog(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(loader.load()));
            stage.showAndWait();
            loadMaintenances();
        } catch (IOException e) { e.printStackTrace(); }
    }
}