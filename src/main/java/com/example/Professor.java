//Ohr Rafaeloff 

// Professor class to store professor information
package com.example;

import java.io.Serializable;

public class Professor implements Serializable {
    private String professor_id;
    private String professor_name;
    private String dept_id; // Storing department ID here instead of name

    public Professor(String id, String name, String departmentId) {
        this.professor_id = id;
        this.professor_name = name;
        this.dept_id = departmentId;
    }

    // Getters and setters
    public String getId() {
        return professor_id;
    }

    public void setId(String id) {
        this.professor_id = id;
    }

    public String getName() {
        return professor_name;
    }

    public void setName(String name) {
        this.professor_name = name;
    }

    public String getDepartmentId() {
        return dept_id;
    }

    public String getDepartment() {
        return dept_id;
    }

    public void setDepartment(String departmentId) {
        this.dept_id = departmentId;
    }
    public void setDepartmentId(String departmentId) {
        this.dept_id = departmentId;
    }
}

