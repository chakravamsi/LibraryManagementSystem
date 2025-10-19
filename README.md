📚 Library Management System

A web-based Library Management System built using Java (Servlets), MySQL, HTML, CSS, and JavaScript (Fetch API). It supports authentication for Admin and Student/Staff users and handles book inventory, issue/return process, fines, and book request workflows.

✅ Features
👨‍💼 Admin Features:

Login authentication for admin.

Dashboard showing total books, issued books, members, active transactions, and fines.

Add, edit, delete books and update quantity.

Add, edit, delete student/staff members.

Search books and members using filters/details.

View, approve, or reject book requests.

View complete transaction history (issued, returned books, fines).

👩‍🎓 Student/Staff Features:

User registration and login.

View available books and search by name, author, category, etc.

Send book request to admin.

View request status: Pending / Approved / Rejected.

View issued books and due dates.

Logout anytime.

💡 Fine Calculation System
User Type	Fine Rule
Student	₹5/day after 14 days from issue
Staff	₹5/day after 30 days from issue

Fine is calculated automatically during return or in transaction records.

🛠️ Tech Stack
Component	Technology Used
Frontend	HTML, CSS, JavaScript (Fetch API)
Backend	Java Servlets
Database	MySQL
Server	Apache Tomcat
Architecture	MVC (Servlets + DAO + HTML/JS)
