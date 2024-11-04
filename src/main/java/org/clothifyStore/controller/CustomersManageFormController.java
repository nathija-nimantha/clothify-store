package org.clothifyStore.controller;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.clothifyStore.db.DBConnection;
import org.clothifyStore.dto.Customer;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class CustomersManageFormController {
    @FXML
    public TextField txtPassword;
    @FXML
    private JFXComboBox<String> cmbGender, cmbNewsLetter;
    @FXML
    private TableColumn<Customer, String> colId, colName, colAddress, colPhoneNumber, colGender, colEmail, colPassword;
    @FXML
    private TableColumn<Customer, LocalDate> colDob;
    @FXML
    private TableColumn<Customer, Boolean> colNewsLetter;
    @FXML
    private DatePicker dob;
    @FXML
    private TableView<Customer> tblCustomers;
    @FXML
    private TextField txtAddress, txtEmail, txtName, txtPhoneNumber, txtId;
    @FXML
    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colNewsLetter.setCellValueFactory(new PropertyValueFactory<>("newsLetter"));

        cmbGender.setItems(FXCollections.observableArrayList("Male", "Female"));
        cmbNewsLetter.setItems(FXCollections.observableArrayList("Yes", "No"));

        loadCustomers();

        tblCustomers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showCustomerDetails(newValue);
            }
        });
    }

    private void showCustomerDetails(Customer customer) {
        txtId.setText(customer.getId());
        txtName.setText(customer.getName());
        txtEmail.setText(customer.getEmail());
        txtAddress.setText(customer.getAddress());
        txtPhoneNumber.setText(customer.getPhoneNumber());
        dob.setValue(customer.getDob());
        cmbGender.setValue(customer.getGender());
        txtPassword.setText(customer.getPassword());
        cmbNewsLetter.setValue(customer.isNewsLetter() ? "Yes" : "No");
    }


    @FXML
    void btnAddOnAction() {
        addCustomer();
    }

    @FXML
    void btnDeleteOnAction() {
        deleteCustomer();
    }

    @FXML
    void btnUpdateOnAction() {
        updateCustomer();
    }

    @FXML
    void btnItemsOnAction(ActionEvent actionEvent) {
        openForm(actionEvent, "/view/staff-dashboarad.fxml", "Clothify Store - Items");
    }

    @FXML
    void btnOrdersOnAction(ActionEvent actionEvent) {
        openForm(actionEvent, "/view/orders-manage-form.fxml", "Clothify Store - Orders");
    }

    private void loadCustomers() {
        customerList.clear();
        String query = "SELECT * FROM customer";

        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getDate("dob").toLocalDate(),
                        rs.getString("phoneNumber"),
                        rs.getString("gender"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("newsLetter")
                );
                customerList.add(customer);
            }

            tblCustomers.setItems(customerList);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error loading customers: " + e.getMessage());
        }
    }

    private void addCustomer() {
        String insertQuery = "INSERT INTO customer (name, email, address, phoneNumber, dob, gender, password, newsLetter) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {

            pstmt.setString(1, txtName.getText());
            pstmt.setString(2, txtEmail.getText());
            pstmt.setString(3, txtAddress.getText());
            pstmt.setString(4, txtPhoneNumber.getText());
            pstmt.setDate(5, Date.valueOf(dob.getValue()));
            pstmt.setString(6, cmbGender.getValue());
            pstmt.setString(7, "defaultPassword");
            pstmt.setBoolean(8, cmbNewsLetter.getValue().equals("Yes"));

            pstmt.executeUpdate();
            loadCustomers();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding customer: " + e.getMessage());
        }
    }

    private void updateCustomer() {
        String updateQuery = "UPDATE customer SET name = ?, email = ?, address = ?, phoneNumber = ?, dob = ?, gender = ?, password = ?, newsLetter = ? WHERE id = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {

            Customer selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();
            if (selectedCustomer == null) {
                return;
            }

            pstmt.setString(1, txtName.getText());
            pstmt.setString(2, txtEmail.getText());
            pstmt.setString(3, txtAddress.getText());
            pstmt.setString(4, txtPhoneNumber.getText());
            pstmt.setDate(5, Date.valueOf(dob.getValue()));
            pstmt.setString(6, cmbGender.getValue());
            pstmt.setString(7, "updatedPassword");
            pstmt.setBoolean(8, cmbNewsLetter.getValue().equals("Yes"));
            pstmt.setString(9, selectedCustomer.getId());

            pstmt.executeUpdate();
            loadCustomers();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating customer: " + e.getMessage());
        }
    }

    private void deleteCustomer() {
        String deleteQuery = "DELETE FROM customer WHERE id = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {

            Customer selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();
            if (selectedCustomer == null) {
                return;
            }

            pstmt.setString(1, selectedCustomer.getId());
            pstmt.executeUpdate();
            loadCustomers();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error deleting customer: " + e.getMessage());
        }
    }

    private void openForm(ActionEvent event, String formPath, String windowName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(formPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(windowName);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
