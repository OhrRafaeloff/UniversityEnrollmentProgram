package com.example;

import java.io.Serializable;

public class Enrollment implements Serializable {
    private static final long serialVersionUID = 1L;
    private String enrollmentID;
    private String enrolledStudentID;
    private String enrolledCourseID;
    private String courseName; // Field for displaying course name
    private String enrollmentYear;
    private String semester;
    private String grade;
    private String studentFirstName;
    private String studentLastName;

    // No-argument constructor
    public Enrollment() {
    }

    // Constructor with arguments
    public Enrollment(String enrollmentId, String studentId, String courseId, String courseName, String year, String semester, String grade, String studentFirstName, String studentLastName) {
        this.enrollmentID = enrollmentId;
        this.enrolledStudentID = studentId;
        this.enrolledCourseID = courseId;
        this.courseName = courseName;
        this.enrollmentYear = year;
        this.semester = semester;
        this.grade = grade;
        this.studentFirstName = studentFirstName;
        this.studentLastName = studentLastName;
    }

    // Getters and Setters
    // Removed unnecessary fields and methods.
    public String getEnrollmentID() {
        return enrollmentID;
    }

    public void setEnrollmentID(String enrollmentID) {
        this.enrollmentID = enrollmentID;
    }

    public String getEnrolledStudentID() {
        return enrolledStudentID;
    }

    public void setEnrolledStudentID(String enrolledStudentID) {
        this.enrolledStudentID = enrolledStudentID;
    }

    public String getEnrolledCourseID() {
        return enrolledCourseID;
    }

    public void setEnrolledCourseID(String enrolledCourseID) {
        this.enrolledCourseID = enrolledCourseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(String enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public void setStudentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public void setStudentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
    }
}
