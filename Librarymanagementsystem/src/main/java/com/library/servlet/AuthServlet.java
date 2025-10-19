package com.library.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.library.dao.MemberDAO;
import com.library.dao.UserDAO;
import com.library.model.Member;
import com.library.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();
    private MemberDAO memberDAO = new MemberDAO();
    private Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/login".equals(pathInfo)) {
                handleLogin(request, response, out);
            } else if ("/register".equals(pathInfo)) {
                handleRegister(request, response, out);
            } else if ("/logout".equals(pathInfo)) {
                handleLogout(request, response, out);
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
    
    private void handleLogin(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        BufferedReader reader = request.getReader();
        JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);
        
        String email = jsonRequest.get("email").getAsString();
        String password = jsonRequest.get("password").getAsString();
        
        User user = userDAO.login(email, password);
        
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole());
            
            // If student or staff, get member info
            if (!user.getRole().equals("ADMIN")) {
                Member member = memberDAO.getMemberByUserId(user.getId());
                if (member != null) {
                    session.setAttribute("memberId", member.getId());
                }
            }
            
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Login successful");
            jsonResponse.addProperty("role", user.getRole());
            jsonResponse.addProperty("name", user.getName());
            
            out.print(gson.toJson(jsonResponse));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\": false, \"message\": \"Invalid email or password\"}");
        }
    }
    
    private void handleRegister(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        BufferedReader reader = request.getReader();
        JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);
        
        String name = jsonRequest.get("name").getAsString();
        String email = jsonRequest.get("email").getAsString();
        String password = jsonRequest.get("password").getAsString();
        String role = jsonRequest.get("role").getAsString();
        String contact = jsonRequest.get("contact").getAsString();
        
        
        if (userDAO.emailExists(email)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Email already registered\"}");
            return;
        }
        
        
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        
        boolean userCreated = userDAO.register(user);
        
        if (userCreated) {
           
            User createdUser = userDAO.login(email, password);
            
            
            Member member = new Member();
            member.setName(name);
            member.setContact(contact);
            member.setRole(role);
            member.setUserId(createdUser.getId());
            
            boolean memberCreated = memberDAO.addMember(member);
            
            if (memberCreated) {
                out.print("{\"success\": true, \"message\": \"Registration successful. Please login.\"}");
            } else {
                out.print("{\"success\": false, \"message\": \"Member creation failed\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Registration failed\"}");
        }
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        out.print("{\"success\": true, \"message\": \"Logged out successfully\"}");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        
        if ("/session".equals(pathInfo)) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("userId") != null) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("userId", (Integer) session.getAttribute("userId"));
                jsonResponse.addProperty("userName", (String) session.getAttribute("userName"));
                jsonResponse.addProperty("userRole", (String) session.getAttribute("userRole"));
                if (session.getAttribute("memberId") != null) {
                    jsonResponse.addProperty("memberId", (Integer) session.getAttribute("memberId"));
                }
                out.print(gson.toJson(jsonResponse));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\": false, \"message\": \"Not authenticated\"}");
            }
        }
        out.flush();
    }
}