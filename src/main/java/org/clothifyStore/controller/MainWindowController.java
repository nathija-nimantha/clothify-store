package org.clothifyStore.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainWindowController {

    private static final Logger LOGGER = Logger.getLogger(MainWindowController.class.getName());
    public JFXButton btnLogin;
    public JFXButton btnSignup;

    public void btnLoginOnAction(ActionEvent event) {
        openForm(event, "/view/login-form.fxml", "Clothify Store - Login");
    }

    public void btnSignupOnAction(ActionEvent event) {
        openForm(event, "/view/signup-form.fxml", "Clothify Store - Sign up");
    }

    private void openForm(ActionEvent event, String formPath, String windowName) {
        try {
            Parent root = loadForm(formPath);
            setStage(event, root, windowName);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load form: " + formPath, e);
        }
    }

    private Parent loadForm(String formPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(formPath));
        return loader.load();
    }

    private void setStage(ActionEvent event, Parent root, String windowName) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(windowName);
        stage.show();
    }
}