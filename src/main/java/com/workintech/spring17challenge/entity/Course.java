package com.workintech.spring17challenge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    private Integer id;
    private String name;
    private Integer credit;
    private Grade grade;
    private int totalGpa;

    public Course(Integer id, String name, Integer credit, Grade grade) {
        this.id = id;
        this.name = name;
        this.credit = credit;
        this.grade = grade;
    }
}
