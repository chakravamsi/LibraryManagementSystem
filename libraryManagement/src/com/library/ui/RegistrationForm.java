package com.library.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.library.util.DBUtil;
import java.sql.*;

public class RegistrationForm {
    
    public void showRegistrationWindow(Stage stage) {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        
        Label title = new Label("Member Registration");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.setMaxWidth(250);
        
        TextField contactField = new TextField();
        contactField.setPromptText("Contact (Phone/Email)");
        contactField.setMaxWidth(250);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(250);
        
        Button registerBtn = new Button("Register");
        registerBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        registerBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String password = passwordField.getText().trim();
            
            if (!name.isEmpty() && !contact.isEmpty() && !password.isEmpty()) {
                if (registerMember(name, contact, password)) {
                    showAlert("Success", "Registration successful! You can now login.");
                    LoginUI loginUI = new LoginUI();
                    loginUI.showLoginWindow(stage);
                } else {
                    showAlert("Error", "Registration failed! Contact might already exist.");
                }
            } else {
                showAlert("Error", "Please fill all fields!");
            }
        });
        
        Button backBtn = new Button("Back to Login");
        backBtn.setOnAction(e -> {
            LoginUI loginUI = new LoginUI();
            loginUI.showLoginWindow(stage);
        });
        
        root.getChildren().addAll(title, nameField, contactField, passwordField, registerBtn, backBtn);
        
        Scene scene = new Scene(root, 400, 350);
        stage.setTitle("Member Registration");
        stage.setScene(scene);
    }
    
    private boolean registerMember(String name, String contact, String password) {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO members (name, contact, password) VALUES (?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, contact);
            ps.setString(3, password);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
