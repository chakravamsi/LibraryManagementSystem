package com.library.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.library.util.DBUtil;
import java.sql.*;

public class LoginUI {
    
    public void showLoginWindow(Stage stage) {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        
        Label title = new Label("Library Management System");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Role selection
        Label roleLabel = new Label("Login as:");
        ToggleGroup roleGroup = new ToggleGroup();
        RadioButton adminRole = new RadioButton("Admin");
        RadioButton memberRole = new RadioButton("Member");
        adminRole.setToggleGroup(roleGroup);
        memberRole.setToggleGroup(roleGroup);
        adminRole.setSelected(true);
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username/Contact");
        usernameField.setMaxWidth(200);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(200);
        
        Button loginBtn = new Button("Login");
        loginBtn.setOnAction(e -> {
            boolean isAdmin = adminRole.isSelected();
            String inputUser = usernameField.getText().trim();
            String inputPass = passwordField.getText().trim();
            
            if (inputUser.isEmpty() || inputPass.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill all fields!");
                alert.showAndWait();
                return;
            }
            
            if (validateLogin(inputUser, inputPass, isAdmin)) {
                if (isAdmin) {
                    DashboardUI dashboard = new DashboardUI();
                    dashboard.showDashboard(stage);
                } else {
                    // Get member ID for member dashboard
                    int memberId = getMemberId(inputUser, inputPass);
                    if (memberId != -1) {
                        MemberDashboardUI memberDashboard = new MemberDashboardUI(memberId);
                        memberDashboard.showMemberDashboard(stage);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Login failed - member not found!");
                        alert.showAndWait();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid credentials!");
                alert.showAndWait();
            }
        });
        
        Button registerBtn = new Button("Register as Member");
        registerBtn.setStyle("-fx-background-color: #4CAF50;");
        registerBtn.setOnAction(e -> {
            RegistrationForm regForm = new RegistrationForm();
            regForm.showRegistrationWindow(stage);
        });
        
        root.getChildren().addAll(title, roleLabel, adminRole, memberRole, usernameField, passwordField, loginBtn, registerBtn);
        
        Scene scene = new Scene(root, 400, 350);
        stage.setTitle("Library Login");
        stage.setScene(scene);
        stage.show();
    }
    
    private boolean validateLogin(String username, String password, boolean isAdmin) {
        try (Connection conn = DBUtil.getConnection()) {
            String query;
            if (isAdmin) {
                query = "SELECT * FROM admins WHERE username = ? AND password = ?";
            } else {
                query = "SELECT * FROM members WHERE contact = ? AND password = ?";
            }
            
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            boolean found = rs.next();
            
            // Debug output - remove this after testing
            System.out.println("Login attempt - User: " + username + ", Admin: " + isAdmin + ", Found: " + found);
            
            return found;
        } catch (SQLException e) {
            System.out.println("SQL Error during login: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private int getMemberId(String contact, String password) {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT member_id FROM members WHERE contact = ? AND password = ?");
            ps.setString(1, contact);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("member_id");
                System.out.println("Found member ID: " + id); // Debug output
                return id;
            }
        } catch (SQLException e) {
            System.out.println("SQL Error getting member ID: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
}
