package com.example.desktop.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class MainLayoutController {

    @FXML private StackPane contentArea;
    @FXML private VBox navLinksContainer;
    @FXML private ComboBox<String> roleComboBox;

    // FXML references for tracking link permissions
    @FXML private Button btnCustomers;
    @FXML private Button btnRentals;
    @FXML private Button btnEmployees;
    @FXML private Button btnKarts;
    @FXML private Button btnEquipments;
    @FXML private Button btnTracks;
    @FXML private Button btnMaintenances;
    @FXML private Button btnRaces;

    @FXML
    public void initialize() {
        // Initialize the role list options
        roleComboBox.setItems(FXCollections.observableArrayList("Receptionist", "Mechanic", "Manager"));

        // Default role on app loading
        roleComboBox.setValue("Receptionist");
        applyRolePermissions("Receptionist");
    }

    @FXML
    private void handleRoleChange() {
        String selectedRole = roleComboBox.getValue();
        if (selectedRole != null) {
            applyRolePermissions(selectedRole);
        }
    }

    private void applyRolePermissions(String role) {
        // Clear current active sidebar layout links
        navLinksContainer.getChildren().clear();

        switch (role) {
            case "Receptionist":
                // Ordered: Rentals, Races, Customers
                navLinksContainer.getChildren().addAll(btnRentals, btnRaces, btnCustomers);
                showRentalsPage(); // Default landing page for Receptionist
                break;

            case "Mechanic":
                // Ordered: Maintenances, Tracks, Karts
                navLinksContainer.getChildren().addAll(btnMaintenances, btnTracks, btnKarts);
                showMaintenancesPage(); // Default landing page for Mechanic
                break;

            case "Manager":
                // Ordered: Employees, Tracks, Karts, Equipments
                navLinksContainer.getChildren().addAll(btnEmployees, btnTracks, btnKarts, btnEquipments);
                showEmployeesPage(); // Default landing page for Manager
                break;
        }
    }

    @FXML private void showCustomersPage() { changePage("/com/example/desktop/customer-view.fxml"); }
    @FXML private void showRentalsPage() { changePage("/com/example/desktop/rental-view.fxml"); }
    @FXML private void showEmployeesPage() { changePage("/com/example/desktop/employee-view.fxml"); }
    @FXML private void showKartsPage() { changePage("/com/example/desktop/kart-view.fxml"); }
    @FXML private void showEquipmentsPage() { changePage("/com/example/desktop/equipment-view.fxml"); }
    @FXML private void showTracksPage() { changePage("/com/example/desktop/track-view.fxml"); }
    @FXML private void showMaintenancesPage() { changePage("/com/example/desktop/maintenance-view.fxml"); }
    @FXML private void showRacesPage() { changePage("/com/example/desktop/race-view.fxml"); }

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