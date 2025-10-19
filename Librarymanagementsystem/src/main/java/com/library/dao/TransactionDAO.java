package com.library.dao;

import com.library.model.Transaction;
import com.library.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TransactionDAO {
    
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT t.*, m.name as member_name, m.role as member_role, b.title as book_title " +
                        "FROM transactions t " +
                        "JOIN members m ON t.member_id = m.id " +
                        "JOIN books b ON t.book_id = b.id " +
                        "ORDER BY t.id DESC";
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setMemberId(rs.getInt("member_id"));
                transaction.setBookId(rs.getInt("book_id"));
                transaction.setIssueDate(rs.getDate("issue_date"));
                transaction.setReturnDate(rs.getDate("return_date"));
                transaction.setFine(rs.getDouble("fine"));
                transaction.setStatus(rs.getString("status"));
                transaction.setMemberName(rs.getString("member_name"));
                transaction.setBookTitle(rs.getString("book_title"));
                transaction.setMemberRole(rs.getString("member_role"));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return transactions;
    }
    
    public List<Transaction> getTransactionsByMemberId(int memberId) {
        List<Transaction> transactions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT t.*, m.name as member_name, m.role as member_role, b.title as book_title " +
                        "FROM transactions t " +
                        "JOIN members m ON t.member_id = m.id " +
                        "JOIN books b ON t.book_id = b.id " +
                        "WHERE t.member_id = ? " +
                        "ORDER BY t.id DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, memberId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setMemberId(rs.getInt("member_id"));
                transaction.setBookId(rs.getInt("book_id"));
                transaction.setIssueDate(rs.getDate("issue_date"));
                transaction.setReturnDate(rs.getDate("return_date"));
                transaction.setFine(rs.getDouble("fine"));
                transaction.setStatus(rs.getString("status"));
                transaction.setMemberName(rs.getString("member_name"));
                transaction.setBookTitle(rs.getString("book_title"));
                transaction.setMemberRole(rs.getString("member_role"));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return transactions;
    }
    
    public boolean issueBook(int memberId, int bookId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO transactions (member_id, book_id, issue_date, status) VALUES (?, ?, CURDATE(), 'ISSUED')";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, memberId);
            stmt.setInt(2, bookId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean returnBook(int transactionId, String memberRole) {
        Connection conn = null;
        PreparedStatement selectStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
           
            String selectSql = "SELECT issue_date FROM transactions WHERE id = ?";
            selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setInt(1, transactionId);
            rs = selectStmt.executeQuery();
            
            if (rs.next()) {
                Date issueDate = rs.getDate("issue_date");
                Date returnDate = new Date(System.currentTimeMillis());
                
                
                double fine = calculateFine(issueDate, returnDate, memberRole);
                
                
                String updateSql = "UPDATE transactions SET return_date = ?, fine = ?, status = 'RETURNED' WHERE id = ?";
                updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setDate(1, returnDate);
                updateStmt.setDouble(2, fine);
                updateStmt.setInt(3, transactionId);
                
                return updateStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (selectStmt != null) selectStmt.close();
                if (updateStmt != null) updateStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    private double calculateFine(Date issueDate, Date returnDate, String memberRole) {
        long diffInMillies = returnDate.getTime() - issueDate.getTime();
        long daysBorrowed = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        
        int allowedDays = memberRole.equals("STUDENT") ? 14 : 30;
        double finePerDay = 5.0;
        
        if (daysBorrowed > allowedDays) {
            return (daysBorrowed - allowedDays) * finePerDay;
        }
        return 0.0;
    }
    
    public double getTotalFinesByMemberId(int memberId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double totalFines = 0.0;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT SUM(fine) as total_fine FROM transactions WHERE member_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, memberId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                totalFines = rs.getDouble("total_fine");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return totalFines;
    }
    
    public double getTotalFinesCollected() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        double totalFines = 0.0;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT SUM(fine) as total_fine FROM transactions";
            rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                totalFines = rs.getDouble("total_fine");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return totalFines;
    }
}