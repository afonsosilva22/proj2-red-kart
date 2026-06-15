package com.example.desktop.controllers;

import com.example.desktop.models.*;
import com.example.desktop.services.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SetupRaceController {

    @FXML private Label lblRentalContext;
    @FXML private ComboBox<Employee> comboEmployee;
    @FXML private ComboBox<Track> comboTrack;
    @FXML private ListView<Kart> listKarts;
    @FXML private ListView<Equipment> listEquipment;

    private final RaceService raceService = new RaceService();
    private final RentalService rentalService = new RentalService();
    private final EmployeeService employeeService = new EmployeeService();
    private final TrackService trackService = new TrackService();
    private final KartService kartService = new KartService();
    private final EquipmentService equipmentService = new EquipmentService();

    private Rental contextualRental;

    @FXML
    public void initialize() {
        listKarts.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listEquipment.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        setupStringConverters();
        loadDatabaseRelations();
    }

    public void setRentalContext(Rental rental) {
        this.contextualRental = rental;
        this.lblRentalContext.setText("Parent Rental Reference ID: " + rental.getId() + " (" + rental.getType().toUpperCase() + ")");
    }

    @FXML
    private void saveRace() {
        try {
            Employee assignedOfficial = comboEmployee.getValue();
            Track assignedTrack = comboTrack.getValue();
            List<Kart> selectedKarts = listKarts.getSelectionModel().getSelectedItems();
            List<Equipment> selectedEquipment = listEquipment.getSelectionModel().getSelectedItems();

            if (assignedOfficial == null || assignedTrack == null) {
                showError("Validation Error", "Please assign an official marshal and circuit layout.");
                return;
            }
            if (selectedKarts.isEmpty()) {
                showError("Validation Error", "A tracking race session requires at least one fleet kart assignment entry.");
                return;
            }

            Race race = new Race();
            race.setRental(contextualRental);
            race.setEmployee(assignedOfficial);
            race.setTrack(assignedTrack);
            race.setStatus("scheduled");

            race.setStartDatetime(contextualRental.getPlannedStartDatetime());
            race.setEndDatetime(contextualRental.getPlannedEndDatetime());

            Set<RaceKart> raceKartSet = new HashSet<>();
            for (Kart k : selectedKarts) {
                RaceKart rk = new RaceKart();
                rk.setRace(race);
                rk.setKart(k);
                rk.setId(new RaceKartId(null, k.getId()));
                raceKartSet.add(rk);
            }
            race.setRaceKarts(raceKartSet);

            Set<RaceEquipment> raceEquipmentSet = new HashSet<>();
            for (Equipment e : selectedEquipment) {
                RaceEquipment re = new RaceEquipment();
                re.setRace(race);
                re.setEquipment(e);
                re.setQuantity(1);
                re.setId(new RaceEquipmentId(null, e.getId()));
                raceEquipmentSet.add(re);
            }
            race.setRaceEquipments(raceEquipmentSet);

            raceService.create(race);

            if (contextualRental.getId() != null
                    && "fully_payed".equalsIgnoreCase(contextualRental.getStatus())) {
                contextualRental.setStatus("ongoing");
                rentalService.update(contextualRental.getId(), contextualRental);
            }

            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
            showError("System Error", "Failed to clear network database compilation workflow: " + e.getMessage());
        }
    }

    @FXML private void closeWindow() { ((Stage) lblRentalContext.getScene().getWindow()).close(); }

    private void loadDatabaseRelations() {
        try {
            // Filter Tracks: Exclude 'closed' layouts
            List<Track> activeTracks = trackService.getAllTracks().stream()
                    .filter(t -> t.getStatus() == null || !t.getStatus().equalsIgnoreCase("closed"))
                    .collect(Collectors.toList());
            comboTrack.setItems(FXCollections.observableArrayList(activeTracks));

            // Filter Karts: Exclude 'scrapped' units
            List<Kart> activeKarts = kartService.getAllKarts().stream()
                    .filter(k -> k.getStatus() == null || !k.getStatus().equalsIgnoreCase("scrapped"))
                    .collect(Collectors.toList());
            listKarts.setItems(FXCollections.observableArrayList(activeKarts));

            // Filter Equipment: Exclude 'scrapped' units
            List<Equipment> activeEquipment = equipmentService.getAllEquipment().stream()
                    .filter(e -> e.getStatus() == null || !e.getStatus().equalsIgnoreCase("scrapped"))
                    .collect(Collectors.toList());
            listEquipment.setItems(FXCollections.observableArrayList(activeEquipment));

            // Load other assets normally
            comboEmployee.setItems(FXCollections.observableArrayList(employeeService.getAllEmployees()));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Loading Error", "Failed to load up-to-date fleet or circuit data from the server.");
        }
    }

    private void setupStringConverters() {
        comboEmployee.setConverter(new StringConverter<Employee>() {
            @Override public String toString(Employee e) { return e == null ? "" : e.getName() + " (ID: " + e.getId() + ")"; }
            @Override public Employee fromString(String s) { return null; }
        });

        comboTrack.setConverter(new StringConverter<Track>() {
            @Override public String toString(Track t) { return t == null ? "" : t.getName() + " (" + t.getLengthKm() + " km)"; }
            @Override public Track fromString(String s) { return null; }
        });

        listKarts.setCellFactory(lv -> new ListCell<Kart>() {
            @Override protected void updateItem(Kart item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : "Kart #" + item.getKartNumber() + " [" + item.getKartTypeName() + "]");
            }
        });

        listEquipment.setCellFactory(lv -> new ListCell<Equipment>() {
            @Override protected void updateItem(Equipment item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getType() + " - " + item.getBrand() + " (" + item.getSize() + ")");
            }
        });
    }

    private void showError(String title, String txt) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(txt);
        alert.showAndWait();
    }
}
