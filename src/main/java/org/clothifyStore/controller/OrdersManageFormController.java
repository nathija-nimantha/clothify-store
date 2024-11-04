package org.clothifyStore.controller;

import com.jfoenix.controls.JFXComboBox;
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
import org.clothifyStore.dto.Orders;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrdersManageFormController {
    @FXML
    private DatePicker OrderDate;
    @FXML
    private JFXComboBox<String> cmbPaymentMethod;
    @FXML
    private JFXComboBox<String> cmbState;
    @FXML
    private TableColumn<Orders, Integer> colCustomerId;
    @FXML
    private TableColumn<Orders, Integer> colId;
    @FXML
    private TableColumn<Orders, String> colOrderDate;
    @FXML
    private TableColumn<Orders, String> colPaymentMethod;
    @FXML
    private TableColumn<Orders, String> colShippingAddress;
    @FXML
    private TableColumn<Orders, String> colStatus;
    @FXML
    private TableColumn<Orders, Double> colTotalAmount;
    @FXML
    private TableColumn<Orders, String> colTrackingNumber;
    @FXML
    private Label lblAdminName;
    @FXML
    private TableView<Orders> tblOrders;
    @FXML
    private TextField txtCustomerId;
    @FXML
    private TextField txtOrderId;
    @FXML
    private TextField txtShippingAddress;
    @FXML
    private TextField txtTotalAmount;
    @FXML
    private TextField txtTrackingNumber;

    private List<Orders> ordersList = new ArrayList<>();

    @FXML
    public void initialize() {
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colPaymentMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colShippingAddress.setCellValueFactory(new PropertyValueFactory<>("shippingAddress"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colTrackingNumber.setCellValueFactory(new PropertyValueFactory<>("trackingNumber"));

        loadOrdersFromDatabase();
        tblOrders.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showOrderDetails(newValue);
            }
        });
    }

    private void loadOrdersFromDatabase() {
        ordersList.clear();
        String query = "SELECT * FROM orders";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Orders order = new Orders(
                        rs.getString("id"),
                        rs.getString("customer_id"),
                        rs.getString("order_date"),
                        rs.getString("status"),
                        rs.getDouble("total_amount"),
                        rs.getString("shipping_address"),
                        rs.getString("payment_method"),
                        rs.getString("tracking_number")
                );
                ordersList.add(order);
            }
            tblOrders.setItems(javafx.collections.FXCollections.observableArrayList(ordersList));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showOrderDetails(Orders order) {
        txtOrderId.setText(String.valueOf(order.getId()));
        txtCustomerId.setText(String.valueOf(order.getCustomerId()));
        OrderDate.setValue(java.time.LocalDate.parse(order.getOrderDate().substring(0, 10)));
        txtShippingAddress.setText(order.getShippingAddress());
        txtTotalAmount.setText(String.valueOf(order.getTotalAmount()));
        cmbPaymentMethod.setValue(order.getPaymentMethod());
        cmbState.setValue(order.getStatus());
        txtTrackingNumber.setText(order.getTrackingNumber());
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        String query = "INSERT INTO orders (customer_id, order_date, status, total_amount, shipping_address, payment_method, tracking_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(txtCustomerId.getText()));
            statement.setString(2, OrderDate.getValue().toString());
            statement.setString(3, cmbState.getValue());
            statement.setDouble(4, Double.parseDouble(txtTotalAmount.getText()));
            statement.setString(5, txtShippingAddress.getText());
            statement.setString(6, cmbPaymentMethod.getValue());
            statement.setString(7, txtTrackingNumber.getText());
            statement.executeUpdate();
            loadOrdersFromDatabase();
            clearInputFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String query = "UPDATE orders SET customer_id=?, order_date=?, status=?, total_amount=?, shipping_address=?, payment_method=?, tracking_number=? WHERE id=?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(txtCustomerId.getText()));
            statement.setString(2, OrderDate.getValue().toString());
            statement.setString(3, cmbState.getValue());
            statement.setDouble(4, Double.parseDouble(txtTotalAmount.getText()));
            statement.setString(5, txtShippingAddress.getText());
            statement.setString(6, cmbPaymentMethod.getValue());
            statement.setString(7, txtTrackingNumber.getText());
            statement.setInt(8, Integer.parseInt(txtOrderId.getText()));
            statement.executeUpdate();
            loadOrdersFromDatabase();
            clearInputFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String query = "DELETE FROM orders WHERE id=?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(txtOrderId.getText()));
            statement.executeUpdate();
            loadOrdersFromDatabase();
            clearInputFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearInputFields() {
        txtOrderId.clear();
        txtCustomerId.clear();
        OrderDate.setValue(null);
        txtShippingAddress.clear();
        txtTotalAmount.clear();
        cmbPaymentMethod.setValue(null);
        cmbState.setValue(null);
        txtTrackingNumber.clear();
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

    public void btnCustomersOnAction(ActionEvent actionEvent) {
        openForm(actionEvent, "/view/customers-manage-form.fxml", "Clothify Store - Customers");
    }

    public void btnItemsOnAction(ActionEvent actionEvent) {
        openForm(actionEvent, "/view/staff-dashboarad.fxml", "Clothify Store - Items");
    }
}
