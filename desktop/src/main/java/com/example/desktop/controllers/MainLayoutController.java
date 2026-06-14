package com.example.desktop.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainLayoutController {

    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        showCustomersPage();
    }

    @FXML
    private void showCustomersPage() {
        changePage("/com/example/desktop/customer-view.fxml");
    }

    @FXML
    private void showRentalsPage() {
        changePage("/com/example/desktop/rental-view.fxml");
    }

    @FXML
    private void showEmployeesPage() {
        changePage("/com/example/desktop/employee-view.fxml");
    }

    @FXML
    private void showKartsPage() {
        changePage("/com/example/desktop/kart-view.fxml");
    }

    @FXML
    private void showEquipmentsPage() {
        changePage("/com/example/desktop/equipment-view.fxml");
    }

    @FXML
    private void showTracksPage() {
        changePage("/com/example/desktop/track-view.fxml");
    }

    @FXML
    private void showMaintenancesPage() {
        changePage("/com/example/desktop/maintenance-view.fxml");
    }

    @FXML
    private void showRacesPage() {
        changePage("/com/example/desktop/race-view.fxml");
    }

    private void changePage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}