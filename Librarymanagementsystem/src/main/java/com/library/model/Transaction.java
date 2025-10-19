package com.library.model;

import java.sql.Date;

public class Transaction {
    private int id;
    private int memberId;
    private int bookId;
    private Date issueDate;
    private Date returnDate;
    private double fine;
    private String status;
    
    
    private String memberName;
    private String bookTitle;
    private String memberRole;
    
    public Transaction() {}
    
    public Transaction(int id, int memberId, int bookId, Date issueDate, 
                      Date returnDate, double fine, String status) {
        this.id = id;
        this.memberId = memberId;
        this.bookId = bookId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.fine = fine;
        this.status = status;
    }
    
   
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    
    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }
    
    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
    
    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public String getMemberRole() { return memberRole; }
    public void setMemberRole(String memberRole) { this.memberRole = memberRole; }
}