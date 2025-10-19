package com.library.model;

public class Member {
    private int id;
    private String name;
    private String contact;
    private String role;
    private int userId;
    
    public Member() {}
    
    public Member(int id, String name, String contact, String role, int userId) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.role = role;
        this.userId = userId;
    }
    
   
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
