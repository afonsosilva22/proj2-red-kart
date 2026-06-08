package com.example.desktop.controllers;

import com.example.desktop.models.Track;
import com.example.desktop.services.TrackService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditTrackController {

    @FXML private TextField txtName;
    @FXML private TextField txtPrice;
    @FXML private TextField txtLength;
    @FXML private TextField txtLimit;
    @FXML private ComboBox<String> comboStatus;

    private final TrackService service = new TrackService();
    private Track targetTrack;

    @FXML
    public void initialize() {
        comboStatus.setItems(FXCollections.observableArrayList("available", "in_use", "maintenance", "closed"));
    }

    public void setTrack(Track track) {
        this.targetTrack = track;
        txtName.setText(track.getName());
        txtPrice.setText(String.valueOf(track.getPricePerHour()));
        txtLength.setText(String.valueOf(track.getLengthKm()));
        txtLimit.setText(String.valueOf(track.getKartLimit()));
        comboStatus.setValue(track.getStatus());
    }

    @FXML
    private void updateTrack() {
        try {
            if (txtName.getText().isBlank() || comboStatus.getValue() == null) return;

            targetTrack.setName(txtName.getText().trim());
            targetTrack.setPricePerHour(Double.parseDouble(txtPrice.getText().trim()));
            targetTrack.setLengthKm(Double.parseDouble(txtLength.getText().trim()));
            targetTrack.setKartLimit(Integer.parseInt(txtLimit.getText().trim()));
            targetTrack.setStatus(comboStatus.getValue());

            service.update(targetTrack.getId(), targetTrack);
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void cancel() { closeWindow(); }
    private void closeWindow() { ((Stage) txtName.getScene().getWindow()).close(); }
}