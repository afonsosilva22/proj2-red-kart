package com.example.desktop.controllers;

import com.example.desktop.models.Employee;
import com.example.desktop.models.Kart;
import com.example.desktop.models.Maintenance;
import com.example.desktop.models.Track;
import com.example.desktop.services.EmployeeService;
import com.example.desktop.services.KartService;
import com.example.desktop.services.MaintenanceService;
import com.example.desktop.services.TrackService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AddMaintenanceController {

    @FXML private ComboBox<String> comboType;
    @FXML private ComboBox<Track> comboTrack;
    @FXML private ComboBox<Kart> comboKart;
    @FXML private ComboBox<String> comboPriority;
    @FXML private ComboBox<Employee> comboEmployee;
    @FXML private TextArea txtDescription;

    private final MaintenanceService maintenanceService = new MaintenanceService();
    private final TrackService trackService = new TrackService();
    private final KartService kartService = new KartService();
    private final EmployeeService employeeService = new EmployeeService();

    @FXML
    public void initialize() {
        // 1. Initialize static dropdowns
        comboType.setItems(FXCollections.observableArrayList("track", "kart"));
        comboPriority.setItems(FXCollections.observableArrayList("low", "normal", "high", "critical"));
        comboPriority.setValue("normal");

        // 2. Setup StringConverters to display readable names
        setupConverters();

        // 3. Fetch targets and employees from the database
        try {
            List<Track> availableTracks = trackService.getAllTracks().stream()
                    .filter(t -> t.getStatus() != null && t.getStatus().equalsIgnoreCase("available"))
                    .collect(Collectors.toList());

            List<Kart> availableKarts = kartService.getAllKarts().stream()
                    .filter(k -> k.getStatus() != null && k.getStatus().equalsIgnoreCase("available"))
                    .collect(Collectors.toList());

            // Fetch employees and filter ONLY for mechanics
            List<Employee> allEmployees = employeeService.getAllEmployees();
            List<Employee> mechanics = allEmployees.stream()
                    .filter(e -> e.getType() != null && e.getType().equalsIgnoreCase("mechanic"))
                    .collect(Collectors.toList());

            comboTrack.setItems(FXCollections.observableArrayList(availableTracks));
            comboKart.setItems(FXCollections.observableArrayList(availableKarts));
            comboEmployee.setItems(FXCollections.observableArrayList(mechanics));

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Connection Error", "Failed to load Tracks, Karts, or Employees from the server.");
        }

        comboType.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("track".equals(newVal)) {
                comboTrack.setDisable(false);
                comboKart.setDisable(true);
                comboKart.setValue(null);
            } else if ("kart".equals(newVal)) {
                comboTrack.setDisable(true);
                comboTrack.setValue(null);
                comboKart.setDisable(false);
            }
        });
    }

    private void setupConverters() {
        comboTrack.setConverter(new StringConverter<Track>() {
            @Override public String toString(Track track) { return track == null ? null : track.getName(); }
            @Override public Track fromString(String string) { return null; }
        });

        comboKart.setConverter(new StringConverter<Kart>() {
            @Override public String toString(Kart kart) { return kart == null ? null : "Kart #" + kart.getKartNumber() + " (" + kart.getKartTypeName() + ")"; }
            @Override public Kart fromString(String string) { return null; }
        });

        comboEmployee.setConverter(new StringConverter<Employee>() {
            @Override
            public String toString(Employee employee) {
                return employee == null ? null : employee.getName() + " (ID: " + employee.getId() + ")";
            }
            @Override public Employee fromString(String string) { return null; }
        });
    }

    @FXML
    private void saveMaintenance() {
        try {
            String type = comboType.getValue();

            // Validation
            if (type == null) {
                showAlert("Validation Error", "Please select a maintenance type.");
                return;
            }
            if ("track".equals(type) && comboTrack.getValue() == null) {
                showAlert("Validation Error", "Please select a target Track.");
                return;
            }
            if ("kart".equals(type) && comboKart.getValue() == null) {
                showAlert("Validation Error", "Please select a target Kart.");
                return;
            }

            // Build payload
            Maintenance m = new Maintenance();
            m.setType(type);
            m.setPriority(comboPriority.getValue());
            m.setDescription(txtDescription.getText() != null ? txtDescription.getText().trim() : "");
            m.setOpenDate(LocalDate.now().toString());
            m.setStatus("open");

            if ("track".equals(type)) {
                Track selectedTrack = comboTrack.getValue();
                selectedTrack.setStatus("maintenance");
                m.setTrack(selectedTrack);
            }

            if ("kart".equals(type)) {
                Kart selectedKart = comboKart.getValue();
                selectedKart.setStatus("maintenance");
                m.setKart(selectedKart);
            }

            if (comboEmployee.getValue() != null) {
                m.setEmployee(comboEmployee.getValue());
            }

            // Send to backend
            maintenanceService.create(m);
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not submit maintenance ticket to the server.");
        }
    }

    @FXML private void cancel() { closeWindow(); }
    private void closeWindow() { ((Stage) comboType.getScene().getWindow()).close(); }

    private void showAlert(String title, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}