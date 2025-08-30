package com.library.controller;

import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;
import com.library.util.DBUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

public class TransactionController {
    
    public ObservableList<Book> getAvailableBooks() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM books WHERE availability_status = TRUE");
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
    
    public ObservableList<Book> getIssuedBooks() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM books WHERE availability_status = FALSE");
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
    
    public ObservableList<Member> getAllMembers() {
        ObservableList<Member> members = FXCollections.observableArrayList();
        try (Connection conn = DBUtil.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM members");
            
            while (rs.next()) {
                members.add(new Member(
                    rs.getInt("member_id"),
                    rs.getString("name"),
                    rs.getString("contact")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }
    
    public boolean issueBook(int bookId, int memberId) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            // Add transaction record
            PreparedStatement ps1 = conn.prepareStatement("INSERT INTO transactions (book_id, member_id, issue_date) VALUES (?, ?, ?)");
            ps1.setInt(1, bookId);
            ps1.setInt(2, memberId);
            ps1.setDate(3, Date.valueOf(LocalDate.now()));
            ps1.executeUpdate();
            
            // Update book availability
            PreparedStatement ps2 = conn.prepareStatement("UPDATE books SET availability_status = FALSE WHERE book_id = ?");
            ps2.setInt(1, bookId);
            ps2.executeUpdate();
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean returnBook(int bookId) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            // Update transaction with return date
            PreparedStatement ps1 = conn.prepareStatement("UPDATE transactions SET return_date = ? WHERE book_id = ? AND return_date IS NULL");
            ps1.setDate(1, Date.valueOf(LocalDate.now()));
            ps1.setInt(2, bookId);
            ps1.executeUpdate();
            
            // Update book availability
            PreparedStatement ps2 = conn.prepareStatement("UPDATE books SET availability_status = TRUE WHERE book_id = ?");
            ps2.setInt(1, bookId);
            ps2.executeUpdate();
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
