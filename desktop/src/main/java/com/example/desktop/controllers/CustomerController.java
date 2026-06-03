package com.example.desktop.controllers;

import com.example.desktop.models.Customer;
import com.example.desktop.services.CustomerService;
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

public class CustomerController {

    @FXML private TableView<Customer> table;
    @FXML private TableColumn<Customer, Integer> colId;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colBirthDate;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colStatus;

    @FXML private Label lblName;
    @FXML private Label lblNif;
    @FXML private Label lblIsMember;
    @FXML private Label lblAddress;
    @FXML private Label lblRegistrationDate;

    @FXML private Button btnEdit;

    private final CustomerService service = new CustomerService();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, customer) -> {
                    if (customer != null) {
                        showCustomerDetails(customer);
                    }
                });

        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> btnEdit.setDisable(newVal == null)
        );

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        loadCustomers();
    }

    @FXML
    private void showCustomerDetails(Customer customer) {
        lblName.setText("Name: " + customer.getName());
        lblNif.setText("NIF: " + customer.getNif());
        lblIsMember.setText("Member: " + customer.getIsMember());
        lblAddress.setText("Address: " + String.format("%s %s, %s %s",
                customer.getStreet()     != null ? customer.getStreet()     : "",
                customer.getDoorNumber() != null ? customer.getDoorNumber() : "",
                customer.getPostalCodeValue() != null ? customer.getPostalCodeValue() : "",
                customer.getLocality() != null ? customer.getLocality() : ""
        ));
        lblRegistrationDate.setText("Registration Date: " + customer.getRegistrationDate());
    }

    @FXML
    public void loadCustomers() {

        try {
            table.setItems(
                    FXCollections.observableArrayList(
                            service.getAllCustomers()
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addCustomer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/desktop/add-customer.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Customer");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editCustomer() {
        Customer selected = table.getSelectionModel().getSelectedItem();

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/desktop/edit-customer.fxml")
            );

            Parent root = loader.load();

            EditCustomerController controller = loader.getController();
            controller.setCustomer(selected);

            Stage stage = new Stage();
            stage.setTitle("Edit Customer");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}