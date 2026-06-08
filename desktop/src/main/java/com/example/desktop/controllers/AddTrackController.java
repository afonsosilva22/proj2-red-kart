package com.example.desktop.controllers;

import com.example.desktop.models.Track;
import com.example.desktop.services.TrackService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddTrackController {

    @FXML private TextField txtName;
    @FXML private TextField txtPrice;
    @FXML private TextField txtLength;
    @FXML private TextField txtLimit;

    private final TrackService service = new TrackService();

    @FXML
    private void saveTrack() {
        try {
            if (txtName.getText().isBlank()) return;

            Track track = new Track();
            track.setName(txtName.getText().trim());
            track.setPricePerHour(Double.parseDouble(txtPrice.getText().trim()));
            track.setLengthKm(Double.parseDouble(txtLength.getText().trim()));
            track.setKartLimit(Integer.parseInt(txtLimit.getText().trim()));

            // Auto-assign default creation status
            track.setStatus("available");

            service.create(track);
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void cancel() { closeWindow(); }
    private void closeWindow() { ((Stage) txtName.getScene().getWindow()).close(); }
}