# 📊 Project Calculator

A simple project estimation tool developed as a 2nd semester exam project for **Alpha Solutions**.  
The system supports basic project breakdown and time calculation.

---

## 🎯 Purpose
Enable users to:
- Break down projects into tasks and subtasks  
- Estimate time usage
- Enable Deadlines 
- Get an overview of total hours and deadlines  

Based on requirements from Alpha Solutions’ kickoff presentation.

---

## ✨ MVP Features
- Create projects, tasks, and subtasks  
- Add estimated hours and optional deadlines  
- Automatic summation of total time  
- Simple UI for viewing project structure

---

## 🛠 Tech Stack
- **Java 21**
- **Spring Boot**
- **JDBC + MySQL**
- **HTML, CSS, Thymeleaf**
- Developed in **IntelliJ IDEA 2025.1**

---

## ▶️ Running the Application
1. Clone the repository  
2. Configure MySQL connection in `application.properties`  
3. The application can be run either via Maven or directly from IntelliJ IDEA.
During development, the project was executed using IntelliJ IDEA by running the main class annotated with @SpringBootApplication.

Alternatively, the application can be started from the command line using:  
   ```bash
   mvn spring-boot:run
