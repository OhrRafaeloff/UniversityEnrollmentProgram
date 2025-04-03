package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DatabaseHelper {

    // INSERT OPERATIONS
    public static void insertStudent(Student student) {
        String insertStudentSQL = "INSERT INTO student (student_id, first_name, last_name, street_address, city, state, zip) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE first_name=VALUES(first_name), last_name=VALUES(last_name), street_address=VALUES(street_address), city=VALUES(city), state=VALUES(state), zip=VALUES(zip)";
        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(insertStudentSQL)) {
            pstmt.setString(1, student.getStuId()); // Setting student_id
            pstmt.setString(2, student.getFirstName());
            pstmt.setString(3, student.getLastName());
            pstmt.setString(4, student.getStreetAddress());
            pstmt.setString(5, student.getCity());
            pstmt.setString(6, student.getState());
            pstmt.setString(7, student.getZip());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static Professor getProfessorById(Connection conn, String professorId) throws SQLException {
        String query = "SELECT professor_id, professor_name, dept_id FROM professor WHERE professor_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, professorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Professor(
                            rs.getString("professor_id"),
                            rs.getString("professor_name"),
                            rs.getString("dept_id"));
                }
            }
        }
        return null;
    }

    public static int getNextAvailableProfessorId(Connection conn) throws SQLException {
        String query = "SELECT MAX(professor_id) FROM professor";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                int maxId = rs.getInt(1);
                return maxId + 1; // Increment max ID by 1 to get the next available ID
            }
        }
        return 1; // Default to 1 if no professors exist
    }

    public static void insertCourse(Course course) {
        String insertCourseSQL = "INSERT INTO course (course_name, professor_id, dept_id) "
                + "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE course_name=VALUES(course_name)";
        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(insertCourseSQL)) {
            pstmt.setString(1, course.getName());
            pstmt.setInt(2, Integer.parseInt(course.getProfessor())); // Assuming professor ID is stored as a string
            pstmt.setInt(3, Integer.parseInt(course.getDept())); // Assuming department ID is stored as a string
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertEnrollment(Enrollment enrollment) {
        String insertEnrollmentSQL = "INSERT INTO enrollment (student_id, course_id, course_name, enrollment_year, semester, grade) "
                + "VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE grade=VALUES(grade)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(insertEnrollmentSQL)) {
    
            // Fetch course_name before inserting
            String courseName = getCourseNameByCourseId(enrollment.getEnrolledCourseID(), conn);
    
            pstmt.setString(1, enrollment.getEnrolledStudentID());
            pstmt.setString(2, enrollment.getEnrolledCourseID());
            pstmt.setString(3, courseName);  // Insert course name
            pstmt.setString(4, enrollment.getEnrollmentYear());
            pstmt.setString(5, enrollment.getSemester());
            pstmt.setString(6, enrollment.getGrade());
    
            pstmt.executeUpdate();
            
            // After inserting/updating enrollment, update the reports table as well.
            insertReport(enrollment);
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static String getCourseNameByCourseId(String courseId, Connection conn) {
        String courseName = "";
        String query = "SELECT course_name FROM course WHERE course_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    courseName = rs.getString("course_name");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return courseName;
    }
    
    public static void insertDepartment(Department department) {
        String insertDepartmentSQL = "INSERT INTO department (dept_id, department_name) "
            + "VALUES (?, ?) ON DUPLICATE KEY UPDATE department_name=VALUES(department_name)";
    try (Connection conn = Database.connect();
         PreparedStatement pstmt = conn.prepareStatement(insertDepartmentSQL)) {
        pstmt.setString(1, department.getId());
        pstmt.setString(2, department.getName());
        pstmt.executeUpdate();
        System.out.println("Department inserted/updated successfully.");
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }

    // METHODS TO SAVE DATA DIRECTLY TO DATABASE
    public static void saveData(LinkedList<Student> students, LinkedList<Course> courses,
            LinkedList<Enrollment> enrollments,
            LinkedList<Professor> professors, LinkedList<Department> departments) {
        try (Connection conn = Database.connect()) {
            if (conn == null) {
                System.out.println("Database connection is null. Unable to save data.");
                return;
            }

            for (Student student : students) {
                insertStudent(student);
            }
            for (Professor professor : professors) {
                insertProfessor(professor);
            }
            for (Course course : courses) {
                insertCourse(course);
            }
            for (Department department : departments) {
                insertDepartment(department);
            }
            for (Enrollment enrollment : enrollments) {
                insertEnrollment(enrollment);
            }

            System.out.println("Data saved successfully to the database!");
        } catch (Exception e) {
            System.out.println("An error occurred while saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // EDIT / UPDATE / GETBYID METHODS

    // GET STUDENT BY ID METHOD
    public static Student getStudentById(String studentId) {
        String query = "SELECT student_id, first_name, last_name, street_address, city, state, zip FROM student WHERE student_id = ?";
        try (Connection conn = Database.connect(); // Open connection here
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getString("student_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("street_address"),
                            rs.getString("city"),
                            rs.getString("state"),
                            rs.getString("zip"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    

    // UPDATE STUDENT METHOD
    public static void updateStudent(Student student) {
        String updateStudentSQL = "UPDATE student SET first_name = ?, last_name = ?, street_address = ?, city = ?, state = ?, zip = ? WHERE student_id = ?";
        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(updateStudentSQL)) {
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setString(3, student.getStreetAddress());
            pstmt.setString(4, student.getCity());
            pstmt.setString(5, student.getState());
            pstmt.setString(6, student.getZip());
            pstmt.setString(7, student.getStuId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // COURSES


    // LOAD METHODS

    public static LinkedList<Student> loadStudents(Connection conn) {
        LinkedList<Student> students = new LinkedList<>();
        String query = "SELECT student_id, first_name, last_name, street_address, city, state, zip FROM student";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Student student = new Student(
                        rs.getString("student_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("street_address"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getString("zip"));
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }



    public static LinkedList<Enrollment> loadEnrollments(Connection conn) {
        LinkedList<Enrollment> enrollments = new LinkedList<>();
        String query = "SELECT e.enrollment_id, e.student_id, e.course_id, c.course_name, e.enrollment_year, e.semester, e.grade, s.first_name, s.last_name "
                + "FROM enrollment e "
                + "JOIN course c ON e.course_id = c.course_id "
                + "JOIN student s ON e.student_id = s.student_id";
    
        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Enrollment enrollment = new Enrollment(
                        rs.getString("enrollment_id"),
                        rs.getString("student_id"),
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getString("enrollment_year"),
                        rs.getString("semester"),
                        rs.getString("grade"),
                        rs.getString("first_name"),
                        rs.getString("last_name"));
                enrollments.add(enrollment);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }
    


    
    public static int getNextAvailableDepartmentId(Connection conn) throws SQLException {
        String query = "SELECT MAX(CAST(dept_id AS UNSIGNED)) AS max_id FROM department";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                return maxId + 1; // Increment max ID by 1 to get the next available ID
            }
        }
        return 1; // Default to 1 if no departments exist
    }
    

    public static Student getStudentById(Connection conn, String studentId) throws SQLException {
        String query = "SELECT student_id, first_name, last_name, street_address, city, state, zip FROM student WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getString("student_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("street_address"),
                            rs.getString("city"),
                            rs.getString("state"),
                            rs.getString("zip"));
                }
            }
        }
        return null;
    }

    public static int getMaxStudentId(Connection conn) {
        String query = "SELECT MAX(student_id) AS max_id FROM student";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("max_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if no student is found or there is an error
    }

    public static Department getDepartmentById(Connection conn, String deptId) throws SQLException {
        String query = "SELECT dept_id, department_name FROM department WHERE dept_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, deptId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Department(
                            rs.getString("dept_id"),
                            rs.getString("department_name"));
                }
            }
        }
        return null;
    }
    
public static void updateDepartment(Connection conn, Department department) throws SQLException {
    String updateDepartmentSQL = "UPDATE department SET department_name = ? WHERE dept_id = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(updateDepartmentSQL)) {
        pstmt.setString(1, department.getName());
        pstmt.setString(2, department.getId());
        pstmt.executeUpdate();
    }
}

    
public static int getMaxCourseId(Connection conn) throws SQLException {
    String query = "SELECT MAX(course_id) FROM courses";
    try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        if (rs.next()) {
            return rs.getInt(1); // Get the max course_id
        } else {
            return 0; // If no courses exist, return 0
        }
    }
}


    // INSERT OR UPDATE PROFESSOR METHOD
    public static void insertProfessor(Professor professor) {
        String insertProfessorSQL = "INSERT INTO professor (professor_id, professor_name, dept_id) "
                + "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE professor_name=VALUES(professor_name), dept_id=VALUES(dept_id)";
        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(insertProfessorSQL)) {
            pstmt.setString(1, professor.getId());
            pstmt.setString(2, professor.getName());
            pstmt.setString(3, professor.getDepartment()); // Assuming department is stored as an ID
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // UPDATE PROFESSOR METHOD
    public static void updateProfessor(Professor professor) {
        String updateProfessorSQL = "UPDATE professor SET professor_name = ?, dept_id = ? WHERE professor_id = ?";
        try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(updateProfessorSQL)) {
            pstmt.setString(1, professor.getName());
            pstmt.setString(2, professor.getDepartment()); // Assuming department is stored as an ID
            pstmt.setString(3, professor.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // LOAD PROFESSORS METHOD
    public static LinkedList<Professor> loadProfessors(Connection conn) {
        LinkedList<Professor> professors = new LinkedList<>();
        String query = "SELECT professor_id, professor_name, dept_id FROM professor";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Professor professor = new Professor(
                        rs.getString("professor_id"),
                        rs.getString("professor_name"),
                        rs.getString("dept_id"));
                professors.add(professor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return professors;
    }

// Get Department by Name
public static Department getDepartmentByName(Connection conn, String deptName) throws SQLException {
    String query = "SELECT dept_id, department_name FROM department WHERE department_name = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, deptName);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return new Department(
                        rs.getString("dept_id"),
                        rs.getString("department_name"));
            }
        }
    }
    return null;
}

// Get Professor by Name
public static Professor getProfessorByName(Connection conn, String professorName) throws SQLException {
    String query = "SELECT professor_id, professor_name, dept_id FROM professor WHERE professor_name = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, professorName);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return new Professor(
                        rs.getString("professor_id"),
                        rs.getString("professor_name"),
                        rs.getString("dept_id"));
            }
        }
    }
    return null;
}

// Insert or Update Course
public static void insertOrUpdateCourse(Course course) {
    String insertOrUpdateCourseSQL = "INSERT INTO course (course_id, course_name, course_desc, dept_id, course_num, professor_id) "
            + "VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE course_name = VALUES(course_name), "
            + "course_desc = VALUES(course_desc), dept_id = VALUES(dept_id), course_num = VALUES(course_num), professor_id = VALUES(professor_id)";
    try (Connection conn = Database.connect();
         PreparedStatement pstmt = conn.prepareStatement(insertOrUpdateCourseSQL)) {
        pstmt.setString(1, course.getId());
        pstmt.setString(2, course.getName());
        pstmt.setString(3, course.getDesc());
        pstmt.setString(4, course.getDept());
        pstmt.setString(5, course.getNum());
        pstmt.setString(6, course.getProfessor());
        pstmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

// Get next available Course ID
public static int getNextAvailableCourseId(Connection conn) throws SQLException {
    String query = "SELECT MAX(CAST(course_id AS UNSIGNED)) AS max_id FROM course";
    try (PreparedStatement pstmt = conn.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
            int maxId = rs.getInt(1);
            return maxId + 1; // Increment max ID by 1 to get the next available ID
        }
    }
    return 1; // Default to 1 if no courses exist
}


// Get Course by ID
public static Course getCourseById(String courseId) throws SQLException {
    String query = "SELECT course_id, course_name, course_desc, dept_id, course_num, professor_id FROM course WHERE course_id = ?";
    try (Connection conn = Database.connect();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, courseId);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return new Course(
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getString("course_desc"),
                        rs.getString("dept_id"),
                        rs.getString("course_num"),
                        rs.getString("professor_id"));
            }
        }
    }
    return null;
}

// Load all Courses
public static LinkedList<Course> loadCourses(Connection conn) throws SQLException {
    LinkedList<Course> courses = new LinkedList<>();
    String query = "SELECT course_id, course_name, course_desc, dept_id, course_num, professor_id FROM course";
    try (PreparedStatement pstmt = conn.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
            Course course = new Course(
                    rs.getString("course_id"),
                    rs.getString("course_name"),
                    rs.getString("course_desc"),
                    rs.getString("dept_id"),
                    rs.getString("course_num"),
                    rs.getString("professor_id"));
            courses.add(course);
        }
    }
    return courses;
}



public static Course getCourseById(Connection conn, String courseId) throws SQLException {
    String query = "SELECT course_id, course_name, course_desc, dept_id, course_num, professor_id FROM course WHERE course_id = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, courseId);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return new Course(
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getString("course_desc"),
                        rs.getString("dept_id"),
                        rs.getString("course_num"),
                        rs.getString("professor_id"));
            }
        }
    }
    return null;
}
public static List<Professor> getProfessorsByDepartmentId(Connection conn, String deptId) throws SQLException {
    List<Professor> professors = new ArrayList<>();
    String query = "SELECT professor_id, professor_name, dept_id FROM professor WHERE dept_id = ?";
    
    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, deptId);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Professor professor = new Professor(
                        rs.getString("professor_id"),
                        rs.getString("professor_name"),
                        rs.getString("dept_id"));
                professors.add(professor);
            }
        }
    }
    
    return professors;
}

public static LinkedList<Department> loadDepartments(Connection conn) throws SQLException {
    LinkedList<Department> departments = new LinkedList<>();
    String query = "SELECT dept_id, department_name FROM department";
    try (PreparedStatement pstmt = conn.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
            Department department = new Department(
                    rs.getString("dept_id"),
                    rs.getString("department_name"));
            departments.add(department);
        }
    }
    return departments;
}



public static List<Professor> getProfessorsByDepartmentName(Connection conn, String deptName) throws SQLException {
    List<Professor> professors = new LinkedList<>();
    String query = "SELECT p.professor_id, p.professor_name, p.dept_id " +
                   "FROM professor p " +
                   "JOIN department d ON p.dept_id = d.dept_id " +
                   "WHERE d.department_name = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, deptName);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Professor professor = new Professor(
                        rs.getString("professor_id"),
                        rs.getString("professor_name"),
                        rs.getString("dept_id"));
                professors.add(professor);
            }
        }
    }
    return professors;
}


public static int getNextAvailableEnrollmentId(Connection conn) throws SQLException {
    String sql = "SELECT MAX(enrollment_id) FROM enrollment";
    try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
        if (rs.next()) {
            return rs.getInt(1) + 1; // Increment the max ID by 1
        } else {
            return 1; // Start from 1 if no enrollments exist
        }
    }
}

public static void insertEnrollment(Connection conn, Enrollment enrollment) throws SQLException {
    String sql = "INSERT INTO enrollment (enrollment_id, student_id, course_id, enrollment_year, semester, grade) VALUES (?, ?, ?, ?, ?, ?)";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, enrollment.getEnrollmentID());
        pstmt.setString(2, enrollment.getEnrolledStudentID());
        pstmt.setString(3, enrollment.getEnrolledCourseID());
        pstmt.setString(4, enrollment.getEnrollmentYear());
        pstmt.setString(5, enrollment.getSemester());
        pstmt.setString(6, enrollment.getGrade());
        pstmt.executeUpdate();
    }
}


public static LinkedList<Enrollment> loadReports(Connection conn) {
    LinkedList<Enrollment> reports = new LinkedList<>();
    String query = "SELECT enrollment_id, student_id, first_name, last_name, course_id, course_name, enrollment_year, semester, grade "
                 + "FROM reports";

    // Ensure the connection is valid
    if (conn == null) {
        System.out.println("Database connection is null. Unable to load reports.");
        return reports;
    }

    try (PreparedStatement pstmt = conn.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) {
        System.out.println("Executing query: " + query);
        while (rs.next()) {
            Enrollment enrollment = new Enrollment(
                    rs.getString("enrollment_id"),    // Maps to enrollmentID
                    rs.getString("student_id"),       // Maps to enrolledStudentID
                    rs.getString("course_id"),        // Maps to enrolledCourseID
                    rs.getString("course_name"),      // Maps to courseName
                    rs.getString("enrollment_year"),  // Maps to enrollmentYear
                    rs.getString("semester"),         // Maps to semester
                    rs.getString("grade"),            // Maps to grade
                    rs.getString("first_name"),       // Maps to studentFirstName
                    rs.getString("last_name")         // Maps to studentLastName
            );
            System.out.println("Fetched enrollment: " + enrollment.getEnrollmentID());
            reports.add(enrollment);
        }
    } catch (SQLException e) {
        System.out.println("An error occurred while loading reports from the database: " + e.getMessage());
        e.printStackTrace();
    }

    if (reports.isEmpty()) {
        System.out.println("No records found in the reports table.");
    } else {
        System.out.println("Loaded " + reports.size() + " records from the reports table.");
    }

    return reports;
}



public static void insertReport(Enrollment enrollment) {
    String insertReportSQL = "INSERT INTO reports (enrollment_id, student_id, first_name, last_name, course_id, course_name, enrollment_year, semester, grade) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) "
                            + "ON DUPLICATE KEY UPDATE grade=VALUES(grade)";
    try (Connection conn = Database.connect();
         PreparedStatement pstmt = conn.prepareStatement(insertReportSQL)) {
        pstmt.setString(1, enrollment.getEnrollmentID());
        pstmt.setString(2, enrollment.getEnrolledStudentID());
        pstmt.setString(3, enrollment.getStudentFirstName());
        pstmt.setString(4, enrollment.getStudentLastName());
        pstmt.setString(5, enrollment.getEnrolledCourseID());
        pstmt.setString(6, enrollment.getCourseName());
        pstmt.setString(7, enrollment.getEnrollmentYear());
        pstmt.setString(8, enrollment.getSemester());
        pstmt.setString(9, enrollment.getGrade());
        pstmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}



}