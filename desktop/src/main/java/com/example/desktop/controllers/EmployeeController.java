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
            java.util.List<Employee> allEmployees = service.getAllEmployees();

            java.util.List<Employee> activeEmployees = allEmployees.stream()
                    .filter(emp -> "active".equalsIgnoreCase(emp.getStatus()))
                    .collect(java.util.stream.Collectors.toList());

            table.setItems(FXCollections.observableArrayList(activeEmployees));
            table.getSelectionModel().clearSelection();

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

    @FXML
    private void editEmployee() {
        Employee selectedEmployee = table.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/desktop/edit-employee.fxml"));
            Parent root = loader.load();

            // Pass the selected record straight into the sub-controller
            EditEmployeeController controller = loader.getController();
            controller.setEmployee(selectedEmployee);

            Stage stage = new Stage();
            stage.setTitle("Edit Employee Profile");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadEmployees(); // Refreshes table view automatically after saving changes
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // In com.example.desktop.controllers.EmployeeController

    @FXML
    private void terminateEmployee() {
        Employee selectedEmployee = table.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) return;

        // Safety verification popup
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Terminate Employment Record");
        alert.setHeaderText("Set " + selectedEmployee.getName() + " to Inactive?");
        alert.setContentText("This will revoke their active status in the system, but their historical records will remain intact.");

        // Wait for user confirmation
        if (alert.showAndWait().orElse(javafx.scene.control.ButtonType.CANCEL) == javafx.scene.control.ButtonType.OK) {
            try {
                // Call API layer soft-delete
                service.terminate(selectedEmployee.getId());

                // Refresh grid automatically
                loadEmployees();

            } catch (Exception e) {
                e.printStackTrace();

                javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                errorAlert.setTitle("Update Error");
                errorAlert.setHeaderText("Action Failed");
                errorAlert.setContentText("Could not update the employee status. Check your server connection.");
                errorAlert.showAndWait();
            }
        }
    }
}