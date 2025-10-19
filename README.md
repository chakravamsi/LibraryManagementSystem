# 📚 Library Management System

A web-based Library Management System built using **Java, Servlets, MySQL, HTML, CSS, and JavaScript**.  
It supports secure login/registration for **Admin and Student/Staff** roles and manages books, members, issuing/returning, fines, and transaction history.

---

## ✅ Features

### 👨‍💼 Admin Module
- Login authentication for admin.
- Dashboard showing total books, issued books, total members, active transactions, and fines.
- Add, edit, delete books and update book quantity.
- Add, edit, delete student/staff members.
- Search books and members by name, ID, category, etc.
- Accept or reject book requests from students/staff.
- View complete transaction history (issued/returned books and fines).

### 👩‍🎓 Student/Staff Module
- User registration and login.
- View available books and search by book name, author, or category.
- Send book request to admin.
- View request status: Pending / Approved / Rejected.
- View list of borrowed books and due dates.
- Logout securely.

---

## 💡 Fine Calculation Rules

| User Type   | Fine Policy                        |
|-------------|-------------------------------------|
| Student     | ₹5 per day after 14 days of issue   |
| Staff       | ₹5 per day after 30 days of issue   |

Fine is calculated automatically during book return or when viewing transaction history.

---

## 🛠️ Tech Stack

| Layer       | Technology Used                     |
|-------------|--------------------------------------|
| Frontend    | HTML, CSS, JavaScript (Fetch API)   |
| Backend     | Java Servlets                       |
| Database    | MySQL                               |
| Server      | Apache Tomcat                       |
| Architecture| MVC (Servlets + DAO + HTML/JS)      |




