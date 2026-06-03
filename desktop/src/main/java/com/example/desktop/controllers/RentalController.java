package com.example.desktop.controllers;

import com.example.desktop.models.Rental;
import com.example.desktop.services.RentalService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;

public class RentalController {

    @FXML private TableView<Rental> rentalTable;
    @FXML private TableColumn<Rental, Integer> colId;
    @FXML private TableColumn<Rental, String> colCustomer;
    @FXML private TableColumn<Rental, String> colStart;
    @FXML private TableColumn<Rental, String> colEnd;
    @FXML private TableColumn<Rental, BigDecimal> colPrice;
    @FXML private TableColumn<Rental, String> colType;
    @FXML private TableColumn<Rental, String> colStatus;

    private final RentalService rentalService = new RentalService();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colStart.setCellValueFactory(new PropertyValueFactory<>("plannedStartDatetime"));
        colEnd.setCellValueFactory(new PropertyValueFactory<>("plannedEndDatetime"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("basePrice"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colCustomer.setCellValueFactory(cellData -> {
            Rental rental = cellData.getValue();
            if (rental != null && rental.getCustomer() != null) {
                return new SimpleStringProperty(rental.getCustomer().getName());
            }
            return new SimpleStringProperty("N/A");
        });

        loadRentals();
    }

    private void loadRentals() {
        try {
            rentalTable.setItems(
                    FXCollections.observableArrayList(rentalService.getAllRentals())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}