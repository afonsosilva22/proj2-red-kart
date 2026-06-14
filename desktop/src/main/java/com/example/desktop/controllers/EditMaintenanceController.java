package com.example.desktop.controllers;

import com.example.desktop.models.Employee;
import com.example.desktop.models.Maintenance;
import com.example.desktop.services.EmployeeService;
import com.example.desktop.services.MaintenanceService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class EditMaintenanceController {

    @FXML private ComboBox<String> comboStatus;
    @FXML private ComboBox<String> comboPriority;
    @FXML private ComboBox<Employee> comboEmployee; // NEW: Reassign Mechanic
    @FXML private TextArea txtDescription;

    private final MaintenanceService service = new MaintenanceService();
    private final EmployeeService employeeService = new EmployeeService(); // NEW

    private Maintenance targetMaintenance;

    @FXML
    public void initialize() {
        // Enforce database constraints explicitly
        comboStatus.setItems(FXCollections.observableArrayList(
                "open", "under_analysis", "diagnosed", "in_repair", "unrepairable", "completed"
        ));
        comboPriority.setItems(FXCollections.observableArrayList(
                "low", "normal", "high", "critical"
        ));

        setupEmployeeDropdown();
    }

    private void setupEmployeeDropdown() {
        // Formatter to show names clearly
        comboEmployee.setConverter(new StringConverter<Employee>() {
            @Override
            public String toString(Employee employee) {
                return employee == null ? "Unassigned" : employee.getName() + " (ID: " + employee.getId() + ")";
            }
            @Override public Employee fromString(String string) { return null; }
        });

        // Fetch Mechanics
        try {
            List<Employee> allEmployees = employeeService.getAllEmployees();
            List<Employee> mechanics = allEmployees.stream()
                    .filter(e -> e.getType() != null && e.getType().equalsIgnoreCase("mechanic"))
                    .collect(Collectors.toList());
            comboEmployee.setItems(FXCollections.observableArrayList(mechanics));
        } catch (Exception e) {
            e.printStackTrace();
            // Non-blocking fail, employees just won't load
        }
    }

    // Called from your main screen when "Edit" is clicked
    public void setMaintenance(Maintenance m) {
        this.targetMaintenance = m;
        comboStatus.setValue(m.getStatus());
        comboPriority.setValue(m.getPriority());
        txtDescription.setText(m.getDescription());

        // Load the currently assigned mechanic (if any)
        if (m.getEmployee() != null) {
            // Find the matching employee object in the dropdown list
            comboEmployee.getItems().stream()
                    .filter(e -> e.getId().equals(m.getEmployee().getId()))
                    .findFirst()
                    .ifPresent(e -> comboEmployee.setValue(e));
        }
    }

    @FXML
    private void updateMaintenance() {
        try {
            String newStatus = comboStatus.getValue();
            String type = targetMaintenance.getType(); // "kart" or "track"

            // 1. Update Core Fields
            targetMaintenance.setStatus(newStatus);
            targetMaintenance.setPriority(comboPriority.getValue());
            targetMaintenance.setDescription(txtDescription.getText() != null ? txtDescription.getText().trim() : "");
            targetMaintenance.setEmployee(comboEmployee.getValue());

            // 2. Handle completion date logging automatically
            if ("completed".equalsIgnoreCase(newStatus) || "unrepairable".equalsIgnoreCase(newStatus)) {
                targetMaintenance.setCompletionDate(LocalDate.now().toString());
            } else {
                targetMaintenance.setCompletionDate(null); // Clear date if reopened
            }

            // 3. ASSET LIFECYCLE STATE TRANSITIONS
            if ("completed".equalsIgnoreCase(newStatus)) {
                if ("kart".equalsIgnoreCase(type) && targetMaintenance.getKart() != null) {
                    targetMaintenance.getKart().setStatus("available");
                } else if ("track".equalsIgnoreCase(type) && targetMaintenance.getTrack() != null) {
                    targetMaintenance.getTrack().setStatus("available");
                }
            }
            else if ("unrepairable".equalsIgnoreCase(newStatus)) {
                if ("kart".equalsIgnoreCase(type) && targetMaintenance.getKart() != null) {
                    targetMaintenance.getKart().setStatus("scrapped"); // Defunct Kart rule
                } else if ("track".equalsIgnoreCase(type) && targetMaintenance.getTrack() != null) {
                    targetMaintenance.getTrack().setStatus("closed");   // Defunct Track rule
                }
            }
            else {
                // Safeguard: If the ticket is still open/in-repair, ensure asset remains in 'maintenance'
                if ("kart".equalsIgnoreCase(type) && targetMaintenance.getKart() != null) {
                    targetMaintenance.getKart().setStatus("maintenance");
                } else if ("track".equalsIgnoreCase(type) && targetMaintenance.getTrack() != null) {
                    targetMaintenance.getTrack().setStatus("maintenance");
                }
            }

            // Send to Backend
            service.update(targetMaintenance.getId(), targetMaintenance);
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Update Failed", "Could not save the changes to the server.");
        }
    }

    @FXML private void cancel() { closeWindow(); }

    private void closeWindow() { ((Stage) comboStatus.getScene().getWindow()).close(); }

    private void showAlert(String title, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}