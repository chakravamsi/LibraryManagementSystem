package com.library.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardUI {
    
    public void showDashboard(Stage stage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        
        Label welcome = new Label("Library Management Dashboard");
        welcome.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        Button booksBtn = new Button("Manage Books");
        Button membersBtn = new Button("Manage Members");
        Button transactionsBtn = new Button("Issue/Return Books");
        Button logoutBtn = new Button("Logout");
        
        
        String btnStyle = "-fx-min-width: 200px; -fx-min-height: 40px; -fx-font-size: 14px;";
        booksBtn.setStyle(btnStyle);
        membersBtn.setStyle(btnStyle);
        transactionsBtn.setStyle(btnStyle);
        logoutBtn.setStyle(btnStyle + "-fx-background-color: #ff6b6b;");
        
        
        booksBtn.setOnAction(e -> {
            BookUI bookUI = new BookUI();
            bookUI.showBookWindow(stage);
        });
        
        membersBtn.setOnAction(e -> {
            MemberUI memberUI = new MemberUI();
            memberUI.showMemberWindow(stage);
        });
        
        transactionsBtn.setOnAction(e -> {
            TransactionUI transactionUI = new TransactionUI();
            transactionUI.showTransactionWindow(stage);
        });
        
        logoutBtn.setOnAction(e -> {
            LoginUI loginUI = new LoginUI();
            loginUI.showLoginWindow(stage);
        });
        
        root.getChildren().addAll(welcome, booksBtn, membersBtn, transactionsBtn, logoutBtn);
        
        Scene scene = new Scene(root, 500, 400);
        stage.setTitle("Library Dashboard");
        stage.setScene(scene);
    }
}

