package com.example.desktop.controllers;

import com.example.desktop.models.Rental;
import com.example.desktop.services.RentalService;
import javafx.beans.property.SimpleObjectProperty;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RentalController {

    @FXML private TableView<Rental> rentalTable;
    @FXML private TableColumn<Rental, Integer> colId;
    @FXML private TableColumn<Rental, String> colCustomer;
    @FXML private TableColumn<Rental, String> colStart;
    @FXML private TableColumn<Rental, String> colEnd;
    @FXML private TableColumn<Rental, BigDecimal> colPrice;
    @FXML private TableColumn<Rental, String> colType;
    @FXML private TableColumn<Rental, String> colStatus;

    // Details Panel Elements
    @FXML private Label lblBasePrice;
    @FXML private Label lblDiscount;
    @FXML private Label lblActualStart;
    @FXML private Label lblActualEnd;
    @FXML private Label lblComplaint;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private final RentalService rentalService = new RentalService();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colStart.setCellValueFactory(new PropertyValueFactory<>("plannedStartDatetime"));
        colEnd.setCellValueFactory(new PropertyValueFactory<>("plannedEndDatetime"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Dynamic Pricing Calculation (Base Price * Discount)
        colPrice.setCellValueFactory(cellData -> {
            Rental rental = cellData.getValue();
            BigDecimal base = rental.getBasePrice();
            BigDecimal discount = rental.getDiscount();

            if (base != null) {
                if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal finalPrice = base.multiply(BigDecimal.ONE.subtract(discount));
                    return new SimpleObjectProperty<>(finalPrice.setScale(2, RoundingMode.HALF_UP));
                }
                return new SimpleObjectProperty<>(base.setScale(2, RoundingMode.HALF_UP));
            }
            return new SimpleObjectProperty<>(BigDecimal.ZERO);
        });

        // Safely parse customer name
        colCustomer.setCellValueFactory(cellData -> {
            Rental rental = cellData.getValue();
            if (rental != null && rental.getCustomer() != null) {
                return new SimpleStringProperty(rental.getCustomer().getName());
            }
            return new SimpleStringProperty("N/A");
        });

        // Details Panel Selection Listener
        rentalTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, rental) -> {
            if (rental != null) {
                showRentalDetails(rental);
            }
        });

        // Toggle action buttons based on selection
        rentalTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean noSelection = (newVal == null);
            btnEdit.setDisable(noSelection);
            btnDelete.setDisable(noSelection);
        });

        loadRentals();
    }

    @FXML
    private void showRentalDetails(Rental rental) {
        lblBasePrice.setText("Base Price: " + (rental.getBasePrice() != null ? rental.getBasePrice() : "N/A"));

        String discountText = "None";
        if (rental.getDiscount() != null && rental.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            discountText = rental.getDiscount().multiply(new BigDecimal("100")).intValue() + "%";
        }
        lblDiscount.setText("Discount: " + discountText);

        lblActualStart.setText("Actual Start: " + (rental.getActualStartDatetime() != null ? rental.getActualStartDatetime() : "Pending"));
        lblActualEnd.setText("Actual End: " + (rental.getActualEndDatetime() != null ? rental.getActualEndDatetime() : "Pending"));
        lblComplaint.setText("Complaint: " + (rental.getComplaint() != null ? rental.getComplaint() : "None"));
    }

    @FXML
    public void loadRentals() {
        try {
            List<Rental> allRentals = rentalService.getAllRentals();

            // UPDATED: Filter out rentals that are 'cancelled' or 'finished'
            List<Rental> visibleRentals = allRentals.stream()
                    .filter(rental -> rental.getStatus() != null
                            && !rental.getStatus().equalsIgnoreCase("cancelled")
                            && !rental.getStatus().equalsIgnoreCase("finished"))
                    .collect(Collectors.toList());

            rentalTable.setItems(FXCollections.observableArrayList(visibleRentals));

            // Clear selection details panel if old selection disappeared
            rentalTable.getSelectionModel().clearSelection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addRental() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/desktop/add-rental.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Rental");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadRentals();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editRental() {
        Rental selected = rentalTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/desktop/edit-rental.fxml"));
            Parent root = loader.load();

            EditRentalController controller = loader.getController();
            controller.setRental(selected);

            Stage stage = new Stage();
            stage.setTitle("Edit Rental Session");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadRentals();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteRental() {
        Rental selected = rentalTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Show confirmation dialog before canceling
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Cancel Rental Session");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to cancel Rental Session ID: " + selected.getId() + "?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Update status locally
                selected.setStatus("cancelled");

                // Sync change down to backend via your updated RentalService
                rentalService.update(selected.getId(), selected);

                // Reload layout table (this will automatically remove it from view)
                loadRentals();

            } catch (Exception e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("System Error");
                errorAlert.setContentText("Failed to cancel the rental session: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }
}