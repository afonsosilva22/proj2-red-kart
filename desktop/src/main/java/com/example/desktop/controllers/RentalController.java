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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class RentalController {

    @FXML private TableView<Rental> rentalTable;
    @FXML private TableColumn<Rental, Integer> colId;
    @FXML private TableColumn<Rental, String> colCustomer;
    @FXML private TableColumn<Rental, String> colStart;
    @FXML private TableColumn<Rental, String> colEnd;
    @FXML private TableColumn<Rental, BigDecimal> colPrice; // Will hold calculated value
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

        // 1. Dynamic Pricing Calculation (Base Price * Discount)
        colPrice.setCellValueFactory(cellData -> {
            Rental rental = cellData.getValue();
            BigDecimal base = rental.getBasePrice();

            // Assume discount is stored as a decimal (e.g., 20% is 0.20)
            // If your DB stores 20% as "20.00", you would need to divide by 100 first!
            BigDecimal discount = rental.getDiscount();

            if (base != null) {
                if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
                    // Calculation: basePrice * (1 - discount)
                    BigDecimal finalPrice = base.multiply(BigDecimal.ONE.subtract(discount));
                    return new SimpleObjectProperty<>(finalPrice.setScale(2, RoundingMode.HALF_UP));
                }
                // Return normal base price if no discount is present
                return new SimpleObjectProperty<>(base.setScale(2, RoundingMode.HALF_UP));
            }
            return new SimpleObjectProperty<>(BigDecimal.ZERO);
        });

        // 2. Safely parse customer name
        colCustomer.setCellValueFactory(cellData -> {
            Rental rental = cellData.getValue();
            if (rental != null && rental.getCustomer() != null) {
                return new SimpleStringProperty(rental.getCustomer().getName());
            }
            return new SimpleStringProperty("N/A");
        });

        // 3. Details Panel Selection Listener
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

        // Display percentage nicely (e.g., 0.20 -> 20%)
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
            rentalTable.setItems(
                    FXCollections.observableArrayList(rentalService.getAllRentals())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addRental() {
        try {
            // Note: Make sure to create this add-rental.fxml file next!
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/desktop/add-rental.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Rental");
            stage.setScene(new Scene(root));

            stage.showAndWait();

            loadRentals(); // Refresh after closing modal

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}