//Ohr Rafaeloff 

package com.example;

import java.io.Serializable;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L; //helps the Java virtual machine verify that the serialized (saved) and deserialized (loaded) objects are compatible in terms of class structure.
    private String course_id;
    private String name;
    private String desc;
    private String dept;
    private String num;
    private String professor;

    public Course(String id, String name, String desc, String dept, String num, String professor) {
        this.course_id = id;
        this.name = name;
        this.desc = desc;
        this.dept = dept;
        this.num = num;
        this.professor = professor;
    }

    // Getters
    public String getId() {
        return course_id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getDept() {
        return dept;
    }

    public String getNum() {
        return num;
    }

    public String getProfessor() {
        return professor;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    @Override
    public String toString() {
        return name + " (" + course_id + ")";
    }
}

