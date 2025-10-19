package com.library.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.library.dao.BookRequestDAO;
import com.library.dao.MemberDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/requests/*")
public class RequestServlet extends HttpServlet {
    private BookRequestDAO requestDAO = new BookRequestDAO();
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
            if ("/all-pending".equals(pathInfo)) {
                
                if (!"ADMIN".equals(userRole)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"success\": false, \"message\": \"Access denied\"}");
                    out.flush();
                    return;
                }
                
                List<Map<String, Object>> requests = requestDAO.getAllPendingRequests();
                out.print(gson.toJson(requests));
                
            } else if ("/my-pending".equals(pathInfo)) {
               
                Integer memberId = (Integer) session.getAttribute("memberId");
                if (memberId == null) {
                    out.print("[]");
                    out.flush();
                    return;
                }
                
                List<Map<String, Object>> requests = requestDAO.getMyPendingRequests(memberId);
                out.print(gson.toJson(requests));
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
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\": false, \"message\": \"Not authenticated\"}");
            out.flush();
            return;
        }
        
        String pathInfo = request.getPathInfo();
        String userRole = (String) session.getAttribute("userRole");
        Integer memberId = (Integer) session.getAttribute("memberId");
        
        try {
            if ("/request".equals(pathInfo)) {
                
                if (!"STUDENT".equals(userRole) && !"STAFF".equals(userRole)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"success\": false, \"message\": \"Only students/staff can request books\"}");
                    out.flush();
                    return;
                }
                
                if (memberId == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\": false, \"message\": \"Member ID not found\"}");
                    out.flush();
                    return;
                }
                
                BufferedReader reader = request.getReader();
                JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);
                int bookId = jsonRequest.get("bookId").getAsInt();
                
                boolean success = requestDAO.requestBook(memberId, bookId);
                
                if (success) {
                    out.print("{\"success\": true, \"message\": \"Book requested successfully\"}");
                } else {
                    out.print("{\"success\": false, \"message\": \"You already have a pending request for this book or request failed\"}");
                }
                
            } else if ("/approve".equals(pathInfo)) {

                if (!"ADMIN".equals(userRole)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"success\": false, \"message\": \"Access denied\"}");
                    out.flush();
                    return;
                }
                
                BufferedReader reader = request.getReader();
                JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);
                int requestId = jsonRequest.get("requestId").getAsInt();
                
                boolean success = requestDAO.approveRequest(requestId);
                
                if (success) {
                    out.print("{\"success\": true, \"message\": \"Request approved and book issued\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"success\": false, \"message\": \"Failed to approve request\"}");
                }
                
            } else if ("/reject".equals(pathInfo)) {
               
                if (!"ADMIN".equals(userRole)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"success\": false, \"message\": \"Access denied\"}");
                    out.flush();
                    return;
                }
                
                BufferedReader reader = request.getReader();
                JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);
                int requestId = jsonRequest.get("requestId").getAsInt();
                String reason = jsonRequest.get("reason").getAsString();
                
                boolean success = requestDAO.rejectRequest(requestId, reason);
                
                if (success) {
                    out.print("{\"success\": true, \"message\": \"Request rejected\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"success\": false, \"message\": \"Failed to reject request\"}");
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
        out.flush();
    }
}