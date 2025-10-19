package com.library.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.library.dao.BookDAO;
import com.library.dao.MemberDAO;
import com.library.dao.TransactionDAO;
import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/transactions/*")
public class TransactionServlet extends HttpServlet {
    private TransactionDAO transactionDAO = new TransactionDAO();
    private BookDAO bookDAO = new BookDAO();
    private MemberDAO memberDAO = new MemberDAO();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\": false, \"message\": \"Not authenticated\"}");
            out.flush();
            return;
        }
        
        String pathInfo = request.getPathInfo();
        String userRole = (String) session.getAttribute("userRole");
        
        try {
            if (pathInfo == null || "/".equals(pathInfo)) {
                
                if ("ADMIN".equals(userRole)) {
                    List<Transaction> transactions = transactionDAO.getAllTransactions();
                    out.print(gson.toJson(transactions));
                } else {
                    Integer memberId = (Integer) session.getAttribute("memberId");
                    if (memberId != null) {
                        List<Transaction> transactions = transactionDAO.getTransactionsByMemberId(memberId);
                        out.print(gson.toJson(transactions));
                    } else {
                        out.print("[]");
                    }
                }
            } else if (pathInfo.startsWith("/fines")) {
               
                Integer memberId = (Integer) session.getAttribute("memberId");
                if (memberId != null) {
                    double totalFines = transactionDAO.getTotalFinesByMemberId(memberId);
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("totalFines", totalFines);
                    out.print(gson.toJson(jsonResponse));
                } else {
                    out.print("{\"totalFines\": 0}");
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
        out.flush();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("userRole"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"success\": false, \"message\": \"Access denied\"}");
            out.flush();
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/issue".equals(pathInfo)) {
                handleIssueBook(request, response, out);
            } else if ("/return".equals(pathInfo)) {
                handleReturnBook(request, response, out);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Endpoint not found\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
        out.flush();
    }
    
    private void handleIssueBook(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        BufferedReader reader = request.getReader();
        JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);
        
        int memberId = jsonRequest.get("memberId").getAsInt();
        int bookId = jsonRequest.get("bookId").getAsInt();
        
       
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"success\": false, \"message\": \"Book not found\"}");
            return;
        }
        
        if (book.getAvailable() <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Book not available\"}");
            return;
        }
        
        
        Member member = memberDAO.getMemberById(memberId);
        if (member == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"success\": false, \"message\": \"Member not found\"}");
            return;
        }
        
        
        boolean issued = transactionDAO.issueBook(memberId, bookId);
        if (issued) {
           
            bookDAO.updateAvailability(bookId, -1);
            out.print("{\"success\": true, \"message\": \"Book issued successfully\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Failed to issue book\"}");
        }
    }
    
    private void handleReturnBook(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        BufferedReader reader = request.getReader();
        JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);
        
        int transactionId = jsonRequest.get("transactionId").getAsInt();
        
        
        List<Transaction> transactions = transactionDAO.getAllTransactions();
        Transaction transaction = null;
        for (Transaction t : transactions) {
            if (t.getId() == transactionId) {
                transaction = t;
                break;
            }
        }
        
        if (transaction == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"success\": false, \"message\": \"Transaction not found\"}");
            return;
        }
        
        if ("RETURNED".equals(transaction.getStatus())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Book already returned\"}");
            return;
        }
        
        
        Member member = memberDAO.getMemberById(transaction.getMemberId());
        String memberRole = member.getRole();
        
        
        boolean returned = transactionDAO.returnBook(transactionId, memberRole);
        if (returned) {
            
            bookDAO.updateAvailability(transaction.getBookId(), 1);
            
            
            List<Transaction> updatedTransactions = transactionDAO.getAllTransactions();
            for (Transaction t : updatedTransactions) {
                if (t.getId() == transactionId) {
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Book returned successfully");
                    jsonResponse.addProperty("fine", t.getFine());
                    out.print(gson.toJson(jsonResponse));
                    return;
                }
            }
            out.print("{\"success\": true, \"message\": \"Book returned successfully\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Failed to return book\"}");
        }
    }
}