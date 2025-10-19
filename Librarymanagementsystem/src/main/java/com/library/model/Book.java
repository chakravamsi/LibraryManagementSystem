package com.library.model;

public class Book {
    private int id;
    private String title;
    private String author;
    private int quantity;
    private int available;
    
    public Book() {}
    
    public Book(int id, String title, String author, int quantity, int available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.quantity = quantity;
        this.available = available;
    }
    
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public int getAvailable() { return available; }
    public void setAvailable(int available) { this.available = available; }
}
