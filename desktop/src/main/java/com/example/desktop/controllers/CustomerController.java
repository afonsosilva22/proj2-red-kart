package com.example.desktop.controllers;

import com.example.desktop.models.Customer;
import com.example.desktop.services.BlacklistEntryService;
import com.example.desktop.services.CustomerService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

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
    @FXML private Button btnBlacklist;
    @FXML private Button btnReinstate;

    private final CustomerService service = new CustomerService();
    private final BlacklistEntryService blacklistService = new BlacklistEntryService();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, customer) -> {
            if (customer != null) {
                showCustomerDetails(customer);

                if ("suspended".equalsIgnoreCase(customer.getStatus())) {
                    btnBlacklist.setVisible(false);
                    btnBlacklist.setManaged(false);

                    btnReinstate.setVisible(true);
                    btnReinstate.setManaged(true);
                } else {
                    btnBlacklist.setVisible(true);
                    btnBlacklist.setManaged(true);

                    btnReinstate.setVisible(false);
                    btnReinstate.setManaged(false);
                }
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    boolean noCustomerSelected = (newVal == null);
                    btnEdit.setDisable(noCustomerSelected);
                    btnBlacklist.setDisable(noCustomerSelected);
                    btnReinstate.setDisable(noCustomerSelected);
                }
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

            stage.showAndWait();

            loadCustomers();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editCustomer() {
        Customer selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

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

            stage.showAndWait();

            loadCustomers();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void blacklistCustomer() {
        Customer selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/desktop/add-blacklist.fxml")
            );
            Parent root = loader.load();

            AddBlacklistController controller = loader.getController();
            controller.setCustomer(selected);

            Stage stage = new Stage();
            stage.setTitle("Blacklist Customer");
            stage.setScene(new Scene(root));

            stage.showAndWait();

            if (controller.isConfirmed()) {
                loadCustomers();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void reinstateCustomer() {
        Customer selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Reinstation");
        alert.setHeaderText("Reinstate " + selected.getName() + "?");
        alert.setContentText("This will close their active blacklist entry and restore their account status to 'active'. Proceed?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                blacklistService.reinstateCustomer(selected.getId());

                loadCustomers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}