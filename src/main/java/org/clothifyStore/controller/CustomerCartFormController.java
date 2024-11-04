package org.clothifyStore.controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.clothifyStore.db.DBConnection;
import org.clothifyStore.dto.Cart;
import org.clothifyStore.dto.Item;
import org.clothifyStore.util.LoginManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerCartFormController {
    @FXML
    private JFXButton btnCheckout, btnHome, btnRemove, btnUpdate;
    @FXML
    private TableColumn<Cart, String> colItemBrand, colItemColor, colItemMaterial, colItemName, colItemSize;
    @FXML
    private TableColumn<Cart, Integer> colItemId, colItemCount;
    @FXML
    private TableColumn<Cart, Double> colItemPrice1;
    @FXML
    private Rectangle itemPage, itemPage1;
    @FXML
    private Label lblCustomerName;
    @FXML
    private TableView<Cart> tblCart;
    @FXML
    private TextField txtItemCount, txtTotalPrice;

    private ObservableList<Cart> cartList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colItemId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colItemBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colItemColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colItemMaterial.setCellValueFactory(new PropertyValueFactory<>("material"));
        colItemSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colItemPrice1.setCellValueFactory(new PropertyValueFactory<>("price"));
        colItemCount.setCellValueFactory(new PropertyValueFactory<>("count"));

        lblCustomerName.setText(LoginManager.getLoggedAdminId());
        loadCartFromDatabase();
        txtTotalPrice.setEditable(false);
    }

    private void loadCartFromDatabase() {
        cartList.clear();
        String query = "SELECT id, name, brand, color, material, size, price, quantity FROM cart";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            double totalPrice = 0;
            while (rs.next()) {
                Cart cartItem = new Cart(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("brand"),
                        rs.getString("color"),
                        rs.getString("material"),
                        rs.getString("size"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                );
                cartList.add(cartItem);
                totalPrice += rs.getDouble("price") * rs.getInt("quantity");
            }
            tblCart.setItems(cartList);
            txtTotalPrice.setText(String.valueOf(totalPrice));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        Cart selectedItem = tblCart.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            try {
                int newCount = Integer.parseInt(txtItemCount.getText());
                if (newCount > 0) {
                    String query = "UPDATE cart SET quantity = ? WHERE id = ?";
                    try (Connection connection = DBConnection.getInstance().getConnection();
                         PreparedStatement stmt = connection.prepareStatement(query)) {
                        stmt.setInt(1, newCount);
                        stmt.setInt(2, selectedItem.getId());
                        stmt.executeUpdate();
                    }
                    loadCartFromDatabase(); // Refresh the cart
                }
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void addItemToCart(Item item, int count) {
        String insertQuery = "INSERT INTO cart (id, name, brand, color, material, size, price, quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, item.getId());
            stmt.setString(2, item.getName());
            stmt.setString(3, item.getBrand());
            stmt.setString(4, item.getColor());
            stmt.setString(5, item.getMaterial());
            stmt.setString(6, item.getSize());
            stmt.setDouble(7, item.getPrice());
            stmt.setInt(8, count);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        Cart selectedItem = tblCart.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String query = "DELETE FROM cart WHERE id = ?";
            try (Connection connection = DBConnection.getInstance().getConnection();
                 PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, selectedItem.getId());
                stmt.executeUpdate();
                loadCartFromDatabase();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btnCheckoutOnAction(ActionEvent event) {
        String orderInsertQuery = "INSERT INTO orders (customer_id, order_date, status, total_amount, shipping_address, payment_method, tracking_number) VALUES (?, NOW(), 'Pending', ?, 'Sample Address', 'Credit Card', 'Tracking123')";
        String orderDetailInsertQuery = "INSERT INTO OrderDetail (order_id, item_id, count) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement orderStmt = connection.prepareStatement(orderInsertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            // Insert order and get generated order ID
            orderStmt.setString(1, LoginManager.getLoggedAdminId());
            orderStmt.setDouble(2, Double.parseDouble(txtTotalPrice.getText()));
            orderStmt.executeUpdate();
            ResultSet rs = orderStmt.getGeneratedKeys();

            if (rs.next()) {
                int orderId = rs.getInt(1);
                // Insert order details
                try (PreparedStatement orderDetailStmt = connection.prepareStatement(orderDetailInsertQuery)) {
                    for (Cart cartItem : cartList) {
                        orderDetailStmt.setInt(1, orderId);
                        orderDetailStmt.setInt(2, cartItem.getId());
                        orderDetailStmt.setInt(3, cartItem.getQuantity());
                        orderDetailStmt.addBatch();
                    }
                    orderDetailStmt.executeBatch();
                }
                // Clear the cart after checkout
                String clearCartQuery = "DELETE FROM cart";
                try (PreparedStatement clearCartStmt = connection.prepareStatement(clearCartQuery)) {
                    clearCartStmt.executeUpdate();
                }
                loadCartFromDatabase(); // Refresh the cart
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void btnHomeOnAction(ActionEvent event) {
        // Close current stage and open the Customer Dashboard
        Stage stage = (Stage) btnHome.getScene().getWindow();
        stage.close();
    }
}
