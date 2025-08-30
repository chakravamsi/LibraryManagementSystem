package com.library.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.library.controller.BookController;
import com.library.model.Book;

public class BookUI {
    private BookController bookController = new BookController();
    private TableView<Book> bookTable = new TableView<>();
    
    public void showBookWindow(Stage stage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        
        // Header
        Label header = new Label("Book Management");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Add book form
        HBox addForm = new HBox(10);
        TextField titleField = new TextField();
        titleField.setPromptText("Book Title");
        titleField.setPrefWidth(200);
        
        TextField authorField = new TextField();
        authorField.setPromptText("Author");
        authorField.setPrefWidth(150);
        
        Button addBtn = new Button("Add Book");
        addBtn.setOnAction(e -> {
            if (!titleField.getText().trim().isEmpty() && !authorField.getText().trim().isEmpty()) {
                if (bookController.addBook(titleField.getText().trim(), authorField.getText().trim())) {
                    titleField.clear();
                    authorField.clear();
                    refreshTable();
                    showAlert("Success", "Book added successfully!");
                } else {
                    showAlert("Error", "Failed to add book!");
                }
            } else {
                showAlert("Error", "Please fill all fields!");
            }
        });
        
        addForm.getChildren().addAll(titleField, authorField, addBtn);
        
        // Table setup
        setupBookTable();
        refreshTable();
        
        // Back button
        Button backBtn = new Button("Back to Dashboard");
        backBtn.setOnAction(e -> {
            DashboardUI dashboard = new DashboardUI();
            dashboard.showDashboard(stage);
        });
        
        root.getChildren().addAll(header, addForm, bookTable, backBtn);
        
        Scene scene = new Scene(root, 700, 500);
        stage.setTitle("Book Management");
        stage.setScene(scene);
    }
    
    private void setupBookTable() {
        TableColumn<Book, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        idCol.setPrefWidth(50);
        
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(250);
        
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(200);
        
        TableColumn<Book, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("availabilityText"));
        statusCol.setPrefWidth(100);
        
        bookTable.getColumns().addAll(idCol, titleCol, authorCol, statusCol);
    }
    
    private void refreshTable() {
        bookTable.setItems(bookController.getAllBooks());
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

