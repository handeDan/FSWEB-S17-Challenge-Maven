package com.workintech.spring17challenge.controller;

import com.workintech.spring17challenge.entity.Course;
import com.workintech.spring17challenge.entity.HighCourseGpa;
import com.workintech.spring17challenge.entity.LowCourseGpa;
import com.workintech.spring17challenge.entity.MediumCourseGpa;
import com.workintech.spring17challenge.exceptions.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final LowCourseGpa lowCourseGpa;
    private final MediumCourseGpa mediumCourseGpa;
    private final HighCourseGpa highCourseGpa;

    private List<Course> courses = new ArrayList<>();

    @Autowired
    public CourseController(LowCourseGpa lowCourseGpa, MediumCourseGpa mediumCourseGpa, HighCourseGpa highCourseGpa) {
        this.lowCourseGpa = lowCourseGpa;
        this.mediumCourseGpa = mediumCourseGpa;
        this.highCourseGpa = highCourseGpa;
    }

    /* @PostConstruct
    public void init() {
        courses = new ArrayList<>();
    } */

    @GetMapping
    public List<Course> getAllCourses() {
        return courses;
    }

    @GetMapping("/{name}")
    public Course getCourseByName(@PathVariable String name) {
        System.out.println(courses);
        return courses.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new ApiException("Course not found", HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Course> addCourse(@RequestBody Course courser) {
        // Aynı isimde course varsa kontrolü:
        Course course = new Course(courser.getId(), courser.getName(), courser.getCredit(), courser.getGrade());
        if (courses.stream().anyMatch(c -> c.getName().equals(course.getName()))) {
            //throw new IllegalArgumentException("A course with the same name already exists.");
        }

        // Kredi değeri 2-4 arasında olmalı kontrolü:
        if (course.getCredit() < 2 || course.getCredit() > 4) {
            throw new IllegalArgumentException("Credit must be between 2 and 4.");
        }

        // Total GPA hesaplama:
        int totalGpa = calculateTotalGpa(course);
        course.setTotalGpa(totalGpa); // totalGpa'yı setle

        courses.add(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(course);  // 201 Created döndür
    }


    //total gpa hesaplama methodu:
    private int calculateTotalGpa(Course course) {
        int gpaCoefficient = course.getGrade().getCoefficient();

        if(course.getCredit() <= 2) {
            return gpaCoefficient * lowCourseGpa.getGpa();
        } else if (course.getCredit() == 3) {
            return gpaCoefficient * mediumCourseGpa.getGpa();
        } else {
            return gpaCoefficient * highCourseGpa.getGpa();
        }
    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable int id, @RequestBody Course updatedCourse) {
        Optional<Course> courseOptional = courses.stream()
                .filter(c -> c.getId() == id)
                .findFirst();

        if (courseOptional.isPresent()) {
            Course existingCourse = courseOptional.get();
            existingCourse.setName(updatedCourse.getName());
            existingCourse.setCredit(updatedCourse.getCredit());
            existingCourse.setGrade(updatedCourse.getGrade());

            int totalGpa = calculateTotalGpa(existingCourse);

            return existingCourse;
        } else {
            throw new IllegalArgumentException("Course not found");
        }

    }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable int id) {
        courses.removeIf(c -> c.getId() == id);
    }



}
