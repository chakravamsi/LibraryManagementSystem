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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private BookDAO bookDAO = new BookDAO();
    private MemberDAO memberDAO = new MemberDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
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
        
        try {
            
            List<Book> books = bookDAO.getAllBooks();
            List<Member> members = memberDAO.getAllMembers();
            List<Transaction> transactions = transactionDAO.getAllTransactions();
            
            int totalBooks = 0;
            int availableBooks = 0;
            int borrowedBooks = 0;
            
            for (Book book : books) {
                totalBooks += book.getQuantity();
                availableBooks += book.getAvailable();
            }
            borrowedBooks = totalBooks - availableBooks;
            
            int totalMembers = members.size();
            double totalFines = transactionDAO.getTotalFinesCollected();
            
            
            int activeTransactions = 0;
            for (Transaction t : transactions) {
                if ("ISSUED".equals(t.getStatus())) {
                    activeTransactions++;
                }
            }
            
            JsonObject stats = new JsonObject();
            stats.addProperty("totalBooks", totalBooks);
            stats.addProperty("availableBooks", availableBooks);
            stats.addProperty("borrowedBooks", borrowedBooks);
            stats.addProperty("totalMembers", totalMembers);
            stats.addProperty("totalFinesCollected", totalFines);
            stats.addProperty("activeTransactions", activeTransactions);
            stats.addProperty("totalTransactions", transactions.size());
            
            out.print(gson.toJson(stats));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
        out.flush();
    }
}