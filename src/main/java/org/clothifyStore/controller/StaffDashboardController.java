package org.clothifyStore.controller;

import com.jfoenix.controls.JFXButton;
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
import org.clothifyStore.dto.Item;
import org.clothifyStore.util.LoginManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffDashboardController {

    public JFXButton btnOrders;
    public JFXButton btnCustomers;
    @FXML
    private JFXButton btnAdd;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXButton btnUpdate;
    @FXML
    private TableView<Item> itemTable;
    @FXML
    private TableColumn<Item, Integer> colId;
    @FXML
    private TableColumn<Item, String> colName;
    @FXML
    private TableColumn<Item, String> colSize;
    @FXML
    private TableColumn<Item, String> colColor;
    @FXML
    private TableColumn<Item, String> colMaterial;
    @FXML
    private TableColumn<Item, String> colBrand;
    @FXML
    private TableColumn<Item, String> colStyle;
    @FXML
    private TableColumn<Item, String> colOccasion;
    @FXML
    private TableColumn<Item, String> colFit;
    @FXML
    private TableColumn<Item, String> colCareInstructions;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtSize;
    @FXML
    private TextField txtColor;
    @FXML
    private TextField txtMaterial;
    @FXML
    private TextField txtBrand;
    @FXML
    private TextField txtStyle;
    @FXML
    private TextField txtOccasion;
    @FXML
    private TextArea txtCareInstructions;
    @FXML
    private TextField txtFit;
    @FXML
    private Label lblAdminName;

    private ObservableList<Item> itemList;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colMaterial.setCellValueFactory(new PropertyValueFactory<>("material"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colStyle.setCellValueFactory(new PropertyValueFactory<>("style"));
        colOccasion.setCellValueFactory(new PropertyValueFactory<>("occasion"));
        colCareInstructions.setCellValueFactory(new PropertyValueFactory<>("careInstructions"));
        colFit.setCellValueFactory(new PropertyValueFactory<>("fit"));

        itemList = FXCollections.observableArrayList();
        loadItemsFromDatabase();

        String adminName = getLoggedAdminName();
        lblAdminName.setText(adminName);

        itemTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showItemDetails(newValue);
            }
        });
    }

    private String getLoggedAdminName() {
        String adminId = LoginManager.getLoggedAdminId();
        String query = "SELECT name FROM admin WHERE id = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, adminId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Admin";
    }

    private void loadItemsFromDatabase() {
        itemList.clear();
        String query = "SELECT * FROM items";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
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

    private void showItemDetails(Item item) {
        txtId.setText(String.valueOf(item.getId()));
        txtName.setText(item.getName());
        txtSize.setText(item.getSize());
        txtColor.setText(item.getColor());
        txtMaterial.setText(item.getMaterial());
        txtBrand.setText(item.getBrand());
        txtStyle.setText(item.getStyle());
        txtOccasion.setText(item.getOccasion());
        txtCareInstructions.setText(item.getCareInstructions());
        txtFit.setText(item.getFit());
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        String name = txtName.getText();
        String size = txtSize.getText();
        String color = txtColor.getText();
        String material = txtMaterial.getText();
        String brand = txtBrand.getText();
        String style = txtStyle.getText();
        String occasion = txtOccasion.getText();
        String careInstructions = txtCareInstructions.getText();
        String fit = txtFit.getText();

        Connection connection = null;

        try {
            connection = DBConnection.getInstance().getConnection();

            if (connection.isClosed()) {
                connection = DBConnection.getInstance().getConnection(); // Reopen the connection
            }

            String query = "INSERT INTO items (name, size, color, material, brand, style, occasion, care_instructions, fit) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, size);
            statement.setString(3, color);
            statement.setString(4, material);
            statement.setString(5, brand);
            statement.setString(6, style);
            statement.setString(7, occasion);
            statement.setString(8, careInstructions);
            statement.setString(9, fit);
            statement.executeUpdate();

            loadItemsFromDatabase();
            clearInputFields();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while connecting to the database.");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        int id = Integer.parseInt(txtId.getText());
        String query = "DELETE FROM items WHERE id=?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            loadItemsFromDatabase();
            clearInputFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        int id = Integer.parseInt(txtId.getText());
        String name = txtName.getText();
        String size = txtSize.getText();
        String color = txtColor.getText();
        String material = txtMaterial.getText();
        String brand = txtBrand.getText();
        String style = txtStyle.getText();
        String occasion = txtOccasion.getText();
        String careInstructions = txtCareInstructions.getText();
        String fit = txtFit.getText();

        String query = "UPDATE items SET name=?, size=?, color=?, material=?, brand=?, style=?, occasion=?, care_instructions=?, fit=? WHERE id=?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, size);
            statement.setString(3, color);
            statement.setString(4, material);
            statement.setString(5, brand);
            statement.setString(6, style);
            statement.setString(7, occasion);
            statement.setString(8, careInstructions);
            statement.setString(9, fit);
            statement.setInt(10, id);
            statement.executeUpdate();
            loadItemsFromDatabase();
            clearInputFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearInputFields() {
        txtId.clear();
        txtName.clear();
        txtSize.clear();
        txtColor.clear();
        txtMaterial.clear();
        txtBrand.clear();
        txtStyle.clear();
        txtOccasion.clear();
        txtCareInstructions.clear();
        txtFit.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    public void btnOrdersOnAction(ActionEvent actionEvent) {
        openForm(actionEvent, "/view/orders-manage-form.fxml", "Clothify Store - Orders");
    }
}
