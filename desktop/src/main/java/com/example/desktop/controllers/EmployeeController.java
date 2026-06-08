package com.example.desktop.controllers;

import com.example.desktop.models.Employee;
import com.example.desktop.services.EmployeeService;
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

public class EmployeeController {

    @FXML private TableView<Employee> table;
    @FXML private TableColumn<Employee, Integer> colId;
    @FXML private TableColumn<Employee, String> colName;
    @FXML private TableColumn<Employee, String> colRole;
    @FXML private TableColumn<Employee, String> colEmail;
    @FXML private TableColumn<Employee, String> colPhone;
    @FXML private TableColumn<Employee, String> colStatus;

    // Detail Panel Bindings
    @FXML private Label lblName;
    @FXML private Label lblUsername;
    @FXML private Label lblNif;
    @FXML private Label lblBirthdate;
    @FXML private Label lblAddress;

    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private final EmployeeService service = new EmployeeService();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("type"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Row Selection Updates Details Pane
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, emp) -> {
            if (emp != null) {
                showEmployeeDetails(emp);
                btnEdit.setDisable(false);
                btnDelete.setDisable(false);
            } else {
                btnEdit.setDisable(true);
                btnDelete.setDisable(true);
            }
        });

        loadEmployees();
    }

    private void showEmployeeDetails(Employee emp) {
        lblName.setText("Name: " + emp.getName());
        lblUsername.setText("Username: " + emp.getUsername());
        lblNif.setText("NIF: " + emp.getNif());
        lblBirthdate.setText("Birthdate: " + (emp.getBirthdate() != null ? emp.getBirthdate() : "N/A"));

        String street = emp.getStreet() != null ? emp.getStreet() : "";
        String door = emp.getDoorNumber() != null ? emp.getDoorNumber() : "";
        String pc = emp.getPostalCodeValue() != null ? emp.getPostalCodeValue() : "";

        lblAddress.setText(String.format("Address: %s %s, %s", street, door, pc).trim());
    }

    @FXML
    public void loadEmployees() {
        try {
            table.setItems(FXCollections.observableArrayList(service.getAllEmployees()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addEmployee() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/desktop/add-employee.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Register New Employee");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadEmployees(); // Refresh layout automatically on window close
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}