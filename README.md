# University Enrollment Program

A Java-based University Enrollment System built as a collaborative project with my Computer Science professor. This project allows users to interact with an enrollment database through API endpoints, managing students, professors, courses, departments, and more.

## 🔧 Features

- Add and manage students, professors, and courses
- API interaction with a SQL database (easily reconfigurable)
- Domain models: `Student`, `Professor`, `Course`, `Department`, `Enrollment`
- Modular Java design with controller classes and helper functions
- External API interaction (e.g., `ZipCodeClient.java`)

## 🛠️ Technologies Used

- Java
- SQL
- RESTful APIs
- MVC-style architecture

## 🗂️ Project Structure

- `Main.java` – Entry point for execution
- `DatabaseHelper.java` – Handles database connection and queries
- `PrimaryController.java` – Core logic and routing
- `Student.java`, `Professor.java` – Object representations of data
- `ZipCodeClient.java` – API interaction

## 🔌 Setup Instructions

1. Clone the repo
2. Open in your preferred Java IDE (e.g., IntelliJ, Eclipse)
3. Update the database URL in `DatabaseHelper.java`
4. Run `Main.java` or `App.java` to start the program

> Note: The original database used for this project has been closed. To run it, simply update the connection string to your own SQL instance.

## 📘 Credits

Developed by **Ohr Rafaeloff** in collaboration with **Professor Patil**, as part of **CS 136** at **West Los Angeles College**.
