package com.example.desktop.controllers;

import com.example.desktop.models.Track;
import com.example.desktop.services.TrackService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class TrackController {

    @FXML private TableView<Track> table;
    @FXML private TableColumn<Track, Integer> colId;
    @FXML private TableColumn<Track, String> colName;
    @FXML private TableColumn<Track, Double> colPrice;
    @FXML private TableColumn<Track, Double> colLength;
    @FXML private TableColumn<Track, Integer> colLimit;
    @FXML private TableColumn<Track, String> colStatus;

    @FXML private Label lblName;
    @FXML private Label lblPrice;
    @FXML private Label lblLength;
    @FXML private Label lblLimit;
    @FXML private Label lblStatus;

    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private final TrackService service = new TrackService();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerHour"));
        colLength.setCellValueFactory(new PropertyValueFactory<>("lengthKm"));
        colLimit.setCellValueFactory(new PropertyValueFactory<>("kartLimit"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, track) -> {
            if (track != null) {
                showTrackDetails(track);
                btnEdit.setDisable(false);
                btnDelete.setDisable(false);
            } else {
                btnEdit.setDisable(true);
                btnDelete.setDisable(true);
            }
        });

        loadTracks();
    }

    @FXML
    public void loadTracks() {
        try {
            java.util.List<Track> allTracks = service.getAllTracks();

            // Updated filter to hide 'closed' tracks from the main view
            java.util.List<Track> activeTracks = allTracks.stream()
                    .filter(t -> !"closed".equalsIgnoreCase(t.getStatus()))
                    .collect(java.util.stream.Collectors.toList());

            table.setItems(FXCollections.observableArrayList(activeTracks));
            table.getSelectionModel().clearSelection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTrackDetails(Track track) {
        lblName.setText("Track Name: " + track.getName());
        lblPrice.setText("Price Per Hour: $" + String.format("%.2f", track.getPricePerHour()));
        lblLength.setText("Circuit Length: " + track.getLengthKm() + " KM");
        lblLimit.setText("Maximum Karts: " + track.getKartLimit());
        lblStatus.setText("Status: " + track.getStatus().toUpperCase());
    }

    @FXML
    private void deleteTrack() {
        Track selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Track Closure");
        confirmation.setHeaderText("Close Circuit: " + selected.getName());
        confirmation.setContentText("Are you sure you want to mark this track as closed? It will be removed from your active views.");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                selected.setStatus("closed");
                service.update(selected.getId(), selected);
                loadTracks();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to close track on the server.");
            }
        }
    }

    @FXML
    private void addTrack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/desktop/add-track.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Register New Track Circuit");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadTracks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editTrack() {
        Track selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/desktop/edit-track.fxml"));
            Parent root = loader.load();
            EditTrackController controller = loader.getController();
            controller.setTrack(selected);
            Stage stage = new Stage();
            stage.setTitle("Modify Circuit Specifications");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadTracks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR); a.setTitle(title); a.setContentText(content); a.showAndWait();
    }
}