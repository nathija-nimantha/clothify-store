package org.clothifyStore.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.clothifyStore.entity.Customer;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class SignupFormController {

    private static final String COUNTRY_CODE = "+94 ";
    public JFXButton btnSignup;
    public CheckBox checkBoxNewsLetter;
    public CheckBox checkBoxTandC;
    public JFXTextField txtPassword;
    public JFXTextField txtEmail;
    public RadioButton RbtnMale;
    public RadioButton RbtnFemale;
    public DatePicker txtDob;
    public JFXTextField txtPhoneNumber;
    public JFXTextField txtAddress;
    public JFXTextField txtLName;
    public JFXTextField txtFName;
    public JFXButton btnLogin;

    @FXML
    private ToggleGroup genderGroup;

    @FXML
    public void initialize() {
        txtPhoneNumber.setText(COUNTRY_CODE);
        txtPhoneNumber.setEditable(true);
        genderGroup = new ToggleGroup();
        RbtnMale.setToggleGroup(genderGroup);
        RbtnFemale.setToggleGroup(genderGroup);

        txtFName.textProperty().addListener((observable, oldValue, newValue) -> checkIfAllFieldsAreFilled());
        txtLName.textProperty().addListener((observable, oldValue, newValue) -> checkIfAllFieldsAreFilled());
        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> checkIfAllFieldsAreFilled());
        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> checkIfAllFieldsAreFilled());
        txtAddress.textProperty().addListener((observable, oldValue, newValue) -> checkIfAllFieldsAreFilled());
        txtDob.valueProperty().addListener((observable, oldValue, newValue) -> checkIfAllFieldsAreFilled());
        txtPhoneNumber.textProperty().addListener((observable, oldValue, newValue) -> checkIfAllFieldsAreFilled());
        genderGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> checkIfAllFieldsAreFilled());
        checkBoxTandC.selectedProperty().addListener((observable, oldValue, newValue) -> checkIfAllFieldsAreFilled());


        txtPhoneNumber.setEditable(true);

        txtPhoneNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.startsWith(COUNTRY_CODE)) {
                txtPhoneNumber.setText(COUNTRY_CODE);
            } else {
                String numberPart = "";
                if (newValue.length() > COUNTRY_CODE.length()) {
                    numberPart = newValue.substring(COUNTRY_CODE.length()).replaceAll("[^\\d]", "");
                }

                if (numberPart.length() > 9) {
                    numberPart = numberPart.substring(0, 9);
                }

                if (numberPart.length() > 0) {
                    if (numberPart.charAt(0) == '0' || (numberPart.charAt(0) != '3' && numberPart.charAt(0) != '7')) {
                        numberPart = numberPart.substring(1);
                    }
                }

                txtPhoneNumber.setText(COUNTRY_CODE + numberPart);
                txtPhoneNumber.positionCaret(txtPhoneNumber.getText().length());
            }
        });
    }

    private void checkIfAllFieldsAreFilled() {
        boolean allFieldsFilled = !txtFName.getText().trim().isEmpty() &&
                !txtLName.getText().trim().isEmpty() &&
                !txtEmail.getText().trim().isEmpty() &&
                !txtPassword.getText().trim().isEmpty() &&
                !txtAddress.getText().trim().isEmpty() &&
                txtDob.getValue() != null &&
                !txtPhoneNumber.getText().trim().isEmpty() &&
                genderGroup.getSelectedToggle() != null &&
                checkBoxTandC.isSelected();
        btnSignup.setDisable(!allFieldsFilled);
    }


    @FXML
    public void handleKeyPress(KeyEvent event) {
        String currentText = txtPhoneNumber.getText();
        if (currentText.length() > COUNTRY_CODE.length()) {
            char typedChar = event.getCharacter().charAt(0);
            if (!Character.isDigit(typedChar)) {
                event.consume();
            }
        }
    }

    public void btnLoginOnAction(ActionEvent actionEvent) {
        openForm(actionEvent, "/view/login-form.fxml", "Clothify Store - Login");
    }

    public void btnSignupOnAction(ActionEvent actionEvent) {
        if (txtDob.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a date of birth.").show();
            return;
        }

        String firstName = txtFName.getText().trim();
        String lastName = txtLName.getText().trim();
        String address = txtAddress.getText().trim();
        String gender = ((RadioButton) genderGroup.getSelectedToggle()).getText();
        LocalDate dob = txtDob.getValue();
        String phoneNumber = txtPhoneNumber.getText();
        boolean enableNewsLetter = checkBoxNewsLetter.isSelected();
        String name = firstName + " " + lastName;
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();
        String id = "CUST" + System.currentTimeMillis();

        org.clothifyStore.dto.Customer customer = new org.clothifyStore.dto.Customer(id, name, address, dob, phoneNumber, gender, email, password, enableNewsLetter);

        Customer customerEntity = new Customer();
        try {
            boolean isSaved = customerEntity.saveCustomer(customer);
            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION, "Customer registered successfully!").show();
                openForm(actionEvent, "/view/login-form.fxml", "Clothify Store - Login");
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to register customer.").show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "An error occurred while registering the customer.").show();
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
