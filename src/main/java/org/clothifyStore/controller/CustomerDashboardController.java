package org.clothifyStore.controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.clothifyStore.db.DBConnection;
import org.clothifyStore.dto.Item;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDashboardController {
    @FXML
    public TableColumn<Item, String> colItemCareInstructions;
    @FXML
    public TableColumn<Item, String> colItemFit;
    @FXML
    public TableColumn<Item, String> colItemStyle;
    @FXML
    public TableColumn<Item, String> colItemOccasion;
    @FXML
    public TableColumn<Item, String> colItemBrand;
    @FXML
    private JFXButton btnCart;
    @FXML
    private JFXButton btnAddToCart;
    @FXML
    private Rectangle itemPage;
    @FXML
    private TableView<Item> itemTable;
    @FXML
    private TableColumn<Item, Integer> colItemId;
    @FXML
    private TableColumn<Item, String> colItemName;
    @FXML
    private TableColumn<Item, String> colItemSize;
    @FXML
    private TableColumn<Item, String> colItemColor;
    @FXML
    private TableColumn<Item, String> colItemMaterial;
    @FXML
    private TableColumn<Item, Double> colItemPrice;
    @FXML
    private Label lblCustomerName;
    @FXML
    private TextField txtItemCount;

    private ObservableList<Item> itemList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        clearCart();
        colItemId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colItemSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colItemColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colItemMaterial.setCellValueFactory(new PropertyValueFactory<>("material"));
        colItemBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colItemStyle.setCellValueFactory(new PropertyValueFactory<>("style"));
        colItemOccasion.setCellValueFactory(new PropertyValueFactory<>("occasion"));
        colItemFit.setCellValueFactory(new PropertyValueFactory<>("fit"));
        colItemCareInstructions.setCellValueFactory(new PropertyValueFactory<>("careInstructions"));
        colItemPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        loadItemsFromDatabase();
    }

    private void loadItemsFromDatabase() {
        itemList.clear();
        String query = "SELECT * FROM items";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("size"),
                        rs.getString("color"),
                        rs.getString("material"),
                        rs.getString("brand"),
                        rs.getString("style"),
                        rs.getString("occasion"),
                        rs.getString("careInstructions"),
                        rs.getString("fit"),
                        rs.getDouble("price")
                );
                itemList.add(item);
            }
            itemTable.setItems(itemList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnCartOnAction(ActionEvent event) {
        openCart();
    }

    @FXML
    void btnAddToCartOnAction(ActionEvent event) {
        Item selectedItem = itemTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null && !txtItemCount.getText().isEmpty()) {
            int itemCount = Integer.parseInt(txtItemCount.getText());
            addItemToCart(selectedItem, itemCount);
        }
    }

    private void addItemToCart(Item item, int count) {
        String insertQuery = "INSERT INTO cart (id, name, brand, color, material, size, price) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, item.getId());
            stmt.setString(2, item.getName());
            stmt.setString(3, item.getBrand());
            stmt.setString(4, item.getColor());
            stmt.setString(5, item.getMaterial());
            stmt.setString(6, item.getSize());
            stmt.setDouble(7, item.getPrice());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openCart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/customer-cart-form.fxml"));
            AnchorPane pane = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.show();
            stage.setOnHiding(event -> clearCart());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Form Error", "An error occurred while loading the form.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    private void clearCart() {
        String deleteQuery = "DELETE FROM cart";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
