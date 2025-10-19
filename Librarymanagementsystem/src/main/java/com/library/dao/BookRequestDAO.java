package com.library.dao;

import com.library.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookRequestDAO {
    
    
    public List<Map<String, Object>> getAllPendingRequests() {
        List<Map<String, Object>> requests = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT br.id, br.member_id, br.book_id, br.status, br.request_date, " +
                        "m.name as member_name, m.role as member_role, b.title as book_title, b.author " +
                        "FROM book_requests br " +
                        "JOIN members m ON br.member_id = m.id " +
                        "JOIN books b ON br.book_id = b.id " +
                        "WHERE br.status = 'PENDING' " +
                        "ORDER BY br.request_date DESC";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Map<String, Object> request = new HashMap<>();
                request.put("id", rs.getInt("id"));
                request.put("memberId", rs.getInt("member_id"));
                request.put("bookId", rs.getInt("book_id"));
                request.put("status", rs.getString("status"));
                request.put("requestDate", rs.getString("request_date"));
                request.put("memberName", rs.getString("member_name"));
                request.put("memberRole", rs.getString("member_role"));
                request.put("bookTitle", rs.getString("book_title"));
                request.put("bookAuthor", rs.getString("author"));
                requests.add(request);
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
        return requests;
    }
    
    
    public List<Map<String, Object>> getMyPendingRequests(int memberId) {
        List<Map<String, Object>> requests = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT br.id, br.member_id, br.book_id, br.status, br.request_date, " +
                        "b.title as book_title, b.author " +
                        "FROM book_requests br " +
                        "JOIN books b ON br.book_id = b.id " +
                        "WHERE br.member_id = ? AND br.status = 'PENDING' " +
                        "ORDER BY br.request_date DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, memberId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> request = new HashMap<>();
                request.put("id", rs.getInt("id"));
                request.put("memberId", rs.getInt("member_id"));
                request.put("bookId", rs.getInt("book_id"));
                request.put("status", rs.getString("status"));
                request.put("requestDate", rs.getString("request_date"));
                request.put("bookTitle", rs.getString("book_title"));
                request.put("bookAuthor", rs.getString("author"));
                requests.add(request);
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
        return requests;
    }
    
    
    public boolean requestBook(int memberId, int bookId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            
            
            String checkSql = "SELECT id FROM book_requests WHERE member_id = ? AND book_id = ? AND status = 'PENDING'";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, memberId);
            checkStmt.setInt(2, bookId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                return false; 
            }
            
            String sql = "INSERT INTO book_requests (member_id, book_id, status) VALUES (?, ?, 'PENDING')";
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
    
    
    public boolean approveRequest(int requestId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            
            String selectSql = "SELECT member_id, book_id FROM book_requests WHERE id = ?";
            stmt = conn.prepareStatement(selectSql);
            stmt.setInt(1, requestId);
            rs = stmt.executeQuery();
            
            if (!rs.next()) {
                return false;
            }
            
            int memberId = rs.getInt("member_id");
            int bookId = rs.getInt("book_id");
            
            
            String updateSql = "UPDATE book_requests SET status = 'APPROVED', approval_date = NOW() WHERE id = ?";
            stmt = conn.prepareStatement(updateSql);
            stmt.setInt(1, requestId);
            stmt.executeUpdate();
            
            
            String transactionSql = "INSERT INTO transactions (member_id, book_id, issue_date, status) VALUES (?, ?, CURDATE(), 'ISSUED')";
            stmt = conn.prepareStatement(transactionSql);
            stmt.setInt(1, memberId);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
            
           
            String updateBookSql = "UPDATE books SET available = available - 1 WHERE id = ?";
            stmt = conn.prepareStatement(updateBookSql);
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    public boolean rejectRequest(int requestId, String reason) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE book_requests SET status = 'REJECTED', rejection_reason = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, reason);
            stmt.setInt(2, requestId);
            
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
}