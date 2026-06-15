package com.example.desktop.controllers;

import com.example.desktop.models.*;
import com.example.desktop.services.RaceService;
import com.example.desktop.services.RentalService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RaceController {

    @FXML private TableView<Race> table;
    @FXML private TableColumn<Race, Integer> colId;
    @FXML private TableColumn<Race, String> colTrack;
    @FXML private TableColumn<Race, String> colOfficial;
    @FXML private TableColumn<Race, String> colStart;
    @FXML private TableColumn<Race, String> colEnd;
    @FXML private TableColumn<Race, String> colStatus;

    @FXML private Label lblTrack;
    @FXML private Label lblOfficial;
    @FXML private Label lblRental;
    @FXML private Label lblTimeline;
    @FXML private Label lblKarts;
    @FXML private Label lblEquipment;

    @FXML private Button btnStart;
    @FXML private Button btnEnd;
    @FXML private Button btnDelete;

    private final RaceService service = new RaceService();
    private final RentalService rentalService = new RentalService();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colStart.setCellValueFactory(new PropertyValueFactory<>("startDatetime"));
        colEnd.setCellValueFactory(new PropertyValueFactory<>("endDatetime"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colTrack.setCellValueFactory(cell -> {
            Track t = cell.getValue().getTrack();
            return new SimpleStringProperty(t != null ? t.getName() : "N/A");
        });

        colOfficial.setCellValueFactory(cell -> {
            Employee e = cell.getValue().getEmployee();
            return new SimpleStringProperty(e != null ? e.getName() : "Unassigned");
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, race) -> {
            if (race != null) {
                showRaceDetails(race);
            }
        });

        // Handles conditional switching and enabling rules for contextual state changes
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                btnStart.setDisable(true);
                btnEnd.setDisable(true);
                btnDelete.setDisable(true);

                btnStart.setVisible(true);
                btnStart.setManaged(true);
                btnEnd.setVisible(false);
                btnEnd.setManaged(false);
            } else {
                btnDelete.setDisable(false);

                if ("ongoing".equalsIgnoreCase(newVal.getStatus())) {
                    btnStart.setVisible(false);
                    btnStart.setManaged(false);

                    btnEnd.setVisible(true);
                    btnEnd.setManaged(true);
                    btnEnd.setDisable(false);
                } else {
                    btnStart.setVisible(true);
                    btnStart.setManaged(true);
                    btnStart.setDisable(false);

                    btnEnd.setVisible(false);
                    btnEnd.setManaged(false);
                }
            }
        });

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        loadRaces();
    }

    @FXML
    private void showRaceDetails(Race race) {
        Track t = race.getTrack();
        Employee e = race.getEmployee();
        Rental r = race.getRental();

        lblTrack.setText("Circuit Track: " + (t != null ? t.getName() + " (" + t.getLengthKm() + " km)" : "N/A"));
        lblOfficial.setText("Assigned Marshal: " + (e != null ? e.getName() + " (ID: " + e.getId() + ")" : "Unassigned"));
        lblRental.setText("Parent Rental Reference ID: " + (r != null ? r.getId() : "N/A"));
        lblTimeline.setText(String.format("Timeline Schedule: From %s to %s",
                race.getStartDatetime() != null ? race.getStartDatetime() : "TBD",
                race.getEndDatetime() != null ? race.getEndDatetime() : "TBD"
        ));

        if (race.getRaceKarts() != null && !race.getRaceKarts().isEmpty()) {
            String kartsJoined = race.getRaceKarts().stream()
                    .filter(rk -> rk.getKart() != null)
                    .map(rk -> "#" + rk.getKart().getKartNumber())
                    .collect(Collectors.joining(", "));
            lblKarts.setText("Deployed Karts: " + kartsJoined);
        } else {
            lblKarts.setText("Deployed Karts: No active units assigned.");
        }

        if (race.getRaceEquipments() != null && !race.getRaceEquipments().isEmpty()) {
            String equipmentJoined = race.getRaceEquipments().stream()
                    .filter(re -> re.getEquipment() != null)
                    .map(re -> re.getEquipment().getType() + " (" + re.getEquipment().getSize() + ")")
                    .collect(Collectors.joining(", "));
            lblEquipment.setText("Allocated Equipment: " + equipmentJoined);
        } else {
            lblEquipment.setText("Allocated Equipment: No equipment checked out.");
        }
    }

    @FXML
    public void loadRaces() {
        try {
            List<Race> allRaces = service.getAllRaces();

            List<Race> filteredRaces = allRaces.stream()
                    .filter(race -> race.getStatus() != null
                            && !"finished".equalsIgnoreCase(race.getStatus().trim())
                            && !"cancelled".equalsIgnoreCase(race.getStatus().trim()))
                    .collect(Collectors.toList());

            table.setItems(FXCollections.observableArrayList(filteredRaces));
            table.getSelectionModel().clearSelection();
            clearDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void startRace() {
        Race selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Start Scheduled Session");
        alert.setHeaderText("Set Race ID #" + selected.getId() + " to Ongoing?");
        alert.setContentText("This will record the track green flag start time right now. Proceed?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                selected.setStatus("ongoing");
                selected.setStartDatetime(Instant.now().toString());

                selected.setEndDatetime(null);

                service.create(selected);
                loadRaces();
            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert("Could not start session execution timeline.");
            }
        }
    }

    @FXML
    private void endRace() {
        Race selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conclude Live Session");
        alert.setHeaderText("Set Race ID #" + selected.getId() + " to Finished?");
        alert.setContentText("This will record completion timestamps, automatically generate lap telemetry, and update kart mileage. Proceed?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                selected.setStatus("finished");
                String endStr = Instant.now().toString();
                selected.setEndDatetime(endStr);

                // ==========================================
                // TELEMETRY & MILEAGE GENERATION LOGIC
                // ==========================================
                if (selected.getStartDatetime() != null && selected.getTrack() != null) {
                    Instant start = Instant.parse(selected.getStartDatetime());
                    Instant end = Instant.parse(endStr);

                    // Calculate how long the race was running
                    long durationSeconds = java.time.Duration.between(start, end).getSeconds();

                    // Get Track length safely
                    double trackLengthKm = Double.parseDouble(String.valueOf(selected.getTrack().getLengthKm()));

                    // Assuming an average speed of 60 km/h (1 km per 60 seconds)
                    double expectedLapTimeSecs = trackLengthKm * 60.0;
                    if (expectedLapTimeSecs < 1) expectedLapTimeSecs = 60.0; // Fallback safeguard

                    // Calculate number of laps based on time elapsed
                    int totalLaps = (int) (durationSeconds / expectedLapTimeSecs);
                    totalLaps = Math.max(1, totalLaps); // Ensure at least 1 lap is recorded

                    // Calculate total mileage driven per kart
                    double mileageAdded = totalLaps * trackLengthKm;

                    if (selected.getRaceKarts() != null) {
                        for (RaceKart rk : selected.getRaceKarts()) {
                            Kart kart = rk.getKart();
                            if (kart != null) {

                                // 1. UPDATE KART MILEAGE (Integer version)
                                int currentMileage = kart.getMileage() != null ? kart.getMileage() : 0;
                                int roundedMileageAdded = (int) Math.ceil(mileageAdded);
                                kart.setMileage(currentMileage + roundedMileageAdded);

                                // 2. GENERATE LAPS
                                java.util.Set<Lap> generatedLaps = new java.util.LinkedHashSet<>();
                                java.util.Random rand = new java.util.Random();

                                for (int i = 1; i <= totalLaps; i++) {
                                    // Randomize the lap time by +/- 5% to look like a real driver
                                    double variation = 0.95 + (0.10 * rand.nextDouble());
                                    java.math.BigDecimal lapTime = java.math.BigDecimal.valueOf(expectedLapTimeSecs * variation)
                                            .setScale(3, java.math.RoundingMode.HALF_UP); // 3 decimal places

                                    LapId lapId = new LapId(selected.getId(), kart.getId(), i);
                                    Lap lap = new Lap(lapId, rk, lapTime);
                                    generatedLaps.add(lap);
                                }

                                // Attach the laps to the RaceKart
                                rk.setLaps(generatedLaps);
                            }
                        }
                    }
                }
                // ==========================================

                service.create(selected); // Post updated race to the backend

                Rental rental = selected.getRental();
                if (rental != null && rental.getId() != null) {
                    rental.setStatus("finished");
                    rental.setActualEndDatetime(endStr);
                    rentalService.update(rental.getId(), rental);
                }

                loadRaces(); // Drops seamlessly since 'finished' is filtered out

            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert("Could not finalize operational metrics data update.");
            }
        }
    }

    @FXML
    private void deleteRace() {
        Race selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Scheduled Race Session");
        alert.setHeaderText("Set Race Event ID #" + selected.getId() + " to Cancelled?");
        alert.setContentText("This updates this session profile status tracking metrics to 'cancelled'. Proceed?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                selected.setStatus("cancelled");







                // Add this right before service.create(selected);
                selected.getRaceKarts().forEach(rk ->
                        System.out.println("FRONTEND: Sending " + rk.getLaps().size() + " laps for Kart #" + rk.getKart().getId())
                );

                service.create(selected);
                loadRaces();
            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert("Could not cancel session profile instance.");
            }
        }
    }

    private void showErrorAlert(String content) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Connection Error");
        errorAlert.setContentText(content + " Check backend connectivity logs.");
        errorAlert.showAndWait();
    }

    private void clearDetails() {
        lblTrack.setText("Circuit Track: ");
        lblOfficial.setText("Assigned Marshal: ");
        lblRental.setText("Parent Rental Reference ID: ");
        lblTimeline.setText("Timeline Schedule: ");
        lblKarts.setText("Deployed Karts: ");
        lblEquipment.setText("Allocated Equipment: ");
    }
}
