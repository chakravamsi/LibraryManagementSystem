package com.library.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.library.controller.MemberController;
import com.library.model.Member;

public class MemberUI {
    private MemberController memberController = new MemberController();
    private TableView<Member> memberTable = new TableView<>();
    
    public void showMemberWindow(Stage stage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        
        // Header
        Label header = new Label("Member Management");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Add member form
        HBox addForm = new HBox(10);
        TextField nameField = new TextField();
        nameField.setPromptText("Member Name");
        nameField.setPrefWidth(200);
        
        TextField contactField = new TextField();
        contactField.setPromptText("Contact (Phone/Email)");
        contactField.setPrefWidth(200);
        
        Button addBtn = new Button("Add Member");
        addBtn.setOnAction(e -> {
            if (!nameField.getText().trim().isEmpty() && !contactField.getText().trim().isEmpty()) {
                if (memberController.addMember(nameField.getText().trim(), contactField.getText().trim())) {
                    nameField.clear();
                    contactField.clear();
                    refreshTable();
                    showAlert("Success", "Member added successfully!");
                } else {
                    showAlert("Error", "Failed to add member!");
                }
            } else {
                showAlert("Error", "Please fill all fields!");
            }
        });
        
        addForm.getChildren().addAll(nameField, contactField, addBtn);
        
        // Table setup
        setupMemberTable();
        refreshTable();
        
        // Back button
        Button backBtn = new Button("Back to Dashboard");
        backBtn.setOnAction(e -> {
            DashboardUI dashboard = new DashboardUI();
            dashboard.showDashboard(stage);
        });
        
        root.getChildren().addAll(header, addForm, memberTable, backBtn);
        
        Scene scene = new Scene(root, 600, 500);
        stage.setTitle("Member Management");
        stage.setScene(scene);
    }
    
    private void setupMemberTable() {
        TableColumn<Member, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        idCol.setPrefWidth(50);
        
        TableColumn<Member, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(250);
        
        TableColumn<Member, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        contactCol.setPrefWidth(250);
        
        memberTable.getColumns().addAll(idCol, nameCol, contactCol);
    }
    
    private void refreshTable() {
        memberTable.setItems(memberController.getAllMembers());
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}