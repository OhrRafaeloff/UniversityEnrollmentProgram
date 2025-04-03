// Ohr Rafaeloff 

// Department class to store department information
package com.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Department implements Serializable {
    private String dept_id;
    private String dept_name;
    private String description; // New field to add more information about the department
    private List<Professor> professors; // List of professors associated with this department

    // Constructor
    public Department(String id, String name) {
        this.dept_id = id;
        this.dept_name = name;
        this.description = "";
        this.professors = new ArrayList<>(); // Initialize empty list of professors
    }

    // Overloaded Constructor with description
    public Department(String id, String name, String description) {
        this.dept_id = id;
        this.dept_name = name;
        this.description = description;
        this.professors = new ArrayList<>(); // Initialize empty list of professors
    }

    // Getters and Setters
    public String getId() {
        return dept_id;
    }

    public void setId(String id) {
        this.dept_id = id;
    }

    public String getName() {
        return dept_name;
    }

    public void setName(String name) {
        this.dept_name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Professor> getProfessors() {
        return professors;
    }

    // Method to add an professor to this department
    public void addProfessor(Professor professor) {
        if (professor != null && !professors.contains(professor)) {
            professors.add(professor);
        }
    }

    // Method to remove an professor from this department
    public void removeProfessor(Professor professor) {
        professors.remove(professor);
    }

    // Method to get the number of professors in this department
    public int getNumberOfProfessors() {
        return professors.size();
    }

    // Method to check if an professor belongs to this department
    public boolean hasProfessor(Professor professor) {
        return professors.contains(professor);
    }

    @Override
    public String toString() {
        return dept_name; // Display department name when used in dropdowns or print statements
    }

    // Method to provide full details of the department (Useful for display)
    public String getFullDetails() {
        return "Department ID: " + dept_id + "\n" +
               "Department Name: " + dept_name + "\n" +
               "Description: " + description + "\n" +
               "Number of Professors: " + getNumberOfProfessors();
    }
}
