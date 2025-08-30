package com.library.model;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private boolean availabilityStatus;
    
    public Book() {}
    
    public Book(int bookId, String title, String author, boolean availabilityStatus) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.availabilityStatus = availabilityStatus;
    }
    
    // Getters and Setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public boolean isAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(boolean availabilityStatus) { this.availabilityStatus = availabilityStatus; }
    
    public String getAvailabilityText() { return availabilityStatus ? "Available" : "Issued"; }
}

