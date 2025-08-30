package com.library.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.library.model.Book;
import com.library.util.DBUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class MemberDashboardUI {
    private int memberId;
    
    public MemberDashboardUI(int memberId) {
        this.memberId = memberId;
    }
    
    public void showMemberDashboard(Stage stage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        
        Label welcome = new Label("Member Dashboard");
        welcome.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Search Available Books Section
        VBox searchSection = new VBox(10);
        Label searchLabel = new Label("Search Available Books");
        searchLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        HBox searchBox = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search by title or author");
        searchField.setPrefWidth(300);
        
        Button searchBtn = new Button("Search");
        searchBox.getChildren().addAll(searchField, searchBtn);
        
        TableView<Book> availableBooksTable = new TableView<>();
        setupBookTable(availableBooksTable);
        availableBooksTable.setPrefHeight(200);
        
        searchBtn.setOnAction(e -> {
            String searchText = searchField.getText().trim();
            availableBooksTable.setItems(searchAvailableBooks(searchText));
        });
        
        // Load all available books initially
        availableBooksTable.setItems(searchAvailableBooks(""));
        
        searchSection.getChildren().addAll(searchLabel, searchBox, availableBooksTable);
        
        // My Issued Books Section
        VBox issuedSection = new VBox(10);
        Label issuedLabel = new Label("My Issued Books");
        issuedLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        TableView<Book> issuedBooksTable = new TableView<>();
        setupBookTable(issuedBooksTable);
        issuedBooksTable.setPrefHeight(150);
        issuedBooksTable.setItems(getMyIssuedBooks());
        
        issuedSection.getChildren().addAll(issuedLabel, issuedBooksTable);
        
        // Logout button
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");
        logoutBtn.setOnAction(e -> {
            LoginUI loginUI = new LoginUI();
            loginUI.showLoginWindow(stage);
        });
        
        root.getChildren().addAll(welcome, searchSection, issuedSection, logoutBtn);
        
        Scene scene = new Scene(root, 700, 600);
        stage.setTitle("Member Dashboard");
        stage.setScene(scene);
    }
    
    private void setupBookTable(TableView<Book> table) {
        TableColumn<Book, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        idCol.setPrefWidth(50);
        
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(300);
        
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(200);
        
        TableColumn<Book, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("availabilityText"));
        statusCol.setPrefWidth(100);
        
        table.getColumns().addAll(idCol, titleCol, authorCol, statusCol);
    }
    
    private ObservableList<Book> searchAvailableBooks(String searchText) {
        ObservableList<Book> books = FXCollections.observableArrayList();
        try (Connection conn = DBUtil.getConnection()) {
            String query = "SELECT * FROM books WHERE availability_status = TRUE";
            if (!searchText.isEmpty()) {
                query += " AND (title LIKE ? OR author LIKE ?)";
            }
            
            PreparedStatement ps = conn.prepareStatement(query);
            if (!searchText.isEmpty()) {
                ps.setString(1, "%" + searchText + "%");
                ps.setString(2, "%" + searchText + "%");
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getBoolean("availability_status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
    
    private ObservableList<Book> getMyIssuedBooks() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT b.* FROM books b JOIN transactions t ON b.book_id = t.book_id " +
                "WHERE t.member_id = ? AND t.return_date IS NULL"
            );
            ps.setInt(1, memberId);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getBoolean("availability_status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}

