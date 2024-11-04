package org.clothifyStore.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.clothifyStore.db.DBConnection;
import org.clothifyStore.util.LoginManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFormController {

    @FXML
    private JFXButton btnLogin;

    @FXML
    private JFXButton btnResetPassword;

    @FXML
    private JFXButton btnSignup;

    @FXML
    private JFXTextField txtEmail;

    @FXML
    private JFXTextField txtPassword;

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Login Error", "Please enter both email and password.");
            return;
        }

        try (Connection connection = DBConnection.getInstance().getConnection()) {
            String customerQuery = "SELECT * FROM customer WHERE email = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(customerQuery);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                LoginManager.setLoggedAdminId(resultSet.getString("id"));
                openForm("/view/customer-dashboard.fxml");
            } else {
                String adminQuery = "SELECT * FROM admin WHERE email = ? AND password = ?";
                preparedStatement = connection.prepareStatement(adminQuery);
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    LoginManager.setLoggedAdminId(resultSet.getString("id"));
                    openForm("/view/staff-dashboarad.fxml");
                } else {
                    showAlert(AlertType.ERROR, "Login Error", "Invalid email or password.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "An error occurred while connecting to the database.");
        }
    }

    @FXML
    void btnSignupOnAction(ActionEvent event) {
        openForm("/view/signup-form.fxml");
    }
    @FXML
    void btnResetPasswordOnAction(ActionEvent event) {
        String email = txtEmail.getText();

        if (email.isEmpty()) {
            showAlert(AlertType.ERROR, "Reset Password Error", "Please enter an email to reset the password.");
            return;
        }

        try (Connection connection = DBConnection.getInstance().getConnection()) {
            String query = "SELECT * FROM customer WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String newPassword = promptNewPassword();
                if (newPassword != null && !newPassword.isEmpty()) {
                    String updateQuery = "UPDATE customer SET password = ? WHERE email = ?";
                    preparedStatement = connection.prepareStatement(updateQuery);
                    preparedStatement.setString(1, newPassword);
                    preparedStatement.setString(2, email);
                    preparedStatement.executeUpdate();

                    showAlert(AlertType.INFORMATION, "Success", "Password successfully updated.");
                }
            } else {
                showAlert(AlertType.ERROR, "Reset Password Error", "Email not found in the system.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "An error occurred while connecting to the database.");
        }
    }

    private void openForm(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane pane = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Form Error", "An error occurred while loading the form.");
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String promptNewPassword() {
        // This can be replaced with an actual dialog or prompt window
        // Here we return a mockup password for simplicity
        return "newpassword123";  // Replace with your actual logic
    }
}
