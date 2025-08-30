package com.library.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.library.controller.TransactionController;
import com.library.model.Book;
import com.library.model.Member;

public class TransactionUI {
    private TransactionController transactionController = new TransactionController();
    
    public void showTransactionWindow(Stage stage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        
        // Header
        Label header = new Label("Issue & Return Books");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Issue Book Section
        VBox issueSection = createIssueSection();
        
        // Return Book Section
        VBox returnSection = createReturnSection();
        
        // Back button
        Button backBtn = new Button("Back to Dashboard");
        backBtn.setOnAction(e -> {
            DashboardUI dashboard = new DashboardUI();
            dashboard.showDashboard(stage);
        });
        
        root.getChildren().addAll(header, issueSection, new Separator(), returnSection, backBtn);
        
        Scene scene = new Scene(root, 600, 500);
        stage.setTitle("Issue & Return Books");
        stage.setScene(scene);
    }
    
    private VBox createIssueSection() {
        VBox section = new VBox(10);
        
        Label issueLabel = new Label("Issue Book");
        issueLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        HBox issueForm = new HBox(10);
        
        ComboBox<Book> bookCombo = new ComboBox<>();
        bookCombo.setPromptText("Select Available Book");
        bookCombo.setPrefWidth(250);
        bookCombo.setItems(transactionController.getAvailableBooks());
        bookCombo.setCellFactory(lv -> new ListCell<Book>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                setText(empty ? null : book.getTitle() + " - " + book.getAuthor());
            }
        });
        bookCombo.setButtonCell(new ListCell<Book>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                setText(empty ? null : book.getTitle() + " - " + book.getAuthor());
            }
        });
        
        ComboBox<Member> memberCombo = new ComboBox<>();
        memberCombo.setPromptText("Select Member");
        memberCombo.setPrefWidth(200);
        memberCombo.setItems(transactionController.getAllMembers());
        memberCombo.setCellFactory(lv -> new ListCell<Member>() {
            @Override
            protected void updateItem(Member member, boolean empty) {
                super.updateItem(member, empty);
                setText(empty ? null : member.getName());
            }
        });
        memberCombo.setButtonCell(new ListCell<Member>() {
            @Override
            protected void updateItem(Member member, boolean empty) {
                super.updateItem(member, empty);
                setText(empty ? null : member.getName());
            }
        });
        
        Button issueBtn = new Button("Issue Book");
        issueBtn.setOnAction(e -> {
            Book selectedBook = bookCombo.getValue();
            Member selectedMember = memberCombo.getValue();
            
            if (selectedBook != null && selectedMember != null) {
                if (transactionController.issueBook(selectedBook.getBookId(), selectedMember.getMemberId())) {
                    bookCombo.setValue(null);
                    memberCombo.setValue(null);
                    bookCombo.setItems(transactionController.getAvailableBooks());
                    showAlert("Success", "Book issued successfully!");
                } else {
                    showAlert("Error", "Failed to issue book!");
                }
            } else {
                showAlert("Error", "Please select both book and member!");
            }
        });
        
        issueForm.getChildren().addAll(bookCombo, memberCombo, issueBtn);
        section.getChildren().addAll(issueLabel, issueForm);
        
        return section;
    }
    
    private VBox createReturnSection() {
        VBox section = new VBox(10);
        
        Label returnLabel = new Label("Return Book");
        returnLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        HBox returnForm = new HBox(10);
        
        ComboBox<Book> issuedBookCombo = new ComboBox<>();
        issuedBookCombo.setPromptText("Select Issued Book");
        issuedBookCombo.setPrefWidth(300);
        issuedBookCombo.setItems(transactionController.getIssuedBooks());
        issuedBookCombo.setCellFactory(lv -> new ListCell<Book>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                setText(empty ? null : book.getTitle() + " - " + book.getAuthor());
            }
        });
        issuedBookCombo.setButtonCell(new ListCell<Book>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                setText(empty ? null : book.getTitle() + " - " + book.getAuthor());
            }
        });
        
        Button returnBtn = new Button("Return Book");
        returnBtn.setOnAction(e -> {
            Book selectedBook = issuedBookCombo.getValue();
            
            if (selectedBook != null) {
                if (transactionController.returnBook(selectedBook.getBookId())) {
                    issuedBookCombo.setValue(null);
                    issuedBookCombo.setItems(transactionController.getIssuedBooks());
                    showAlert("Success", "Book returned successfully!");
                } else {
                    showAlert("Error", "Failed to return book!");
                }
            } else {
                showAlert("Error", "Please select a book to return!");
            }
        });
        
        returnForm.getChildren().addAll(issuedBookCombo, returnBtn);
        section.getChildren().addAll(returnLabel, returnForm);
        
        return section;
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
