package com.vlabs.lms.controller;

import java.util.List;

import com.vlabs.lms.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vlabs.lms.model.Course;

@RestController
@RequestMapping("${lms.rest.base-url}")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("getAllCourses")
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @GetMapping("getCoursesByTechnology")
    public List<Course> getCoursesByTechnology(@RequestParam String technology) {
        return courseRepository.getCoursesByTechnologyRegEx(technology);
    }

    @GetMapping("getCoursesByDuration")
    public List<Course> getCoursesByDuration(@RequestParam int fromDuration, @RequestParam int toDuration) {
        return courseRepository.findByDurationBetween(fromDuration, toDuration);
    }

    @PostMapping("addOrUpdateCourse")
    public ResponseEntity<String> addorUpdateCourse(@RequestBody Course course) {
        Course courseByName = courseRepository.findByName(course.getName());
        Course savedOrUpdated = null;
        if (courseByName != null) {
            course.setId(courseByName.getId());
            savedOrUpdated = courseRepository.save(course);
            return ResponseEntity.status(HttpStatus.OK).body("Course updated successfully.");
        }
        savedOrUpdated = courseRepository.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).body("Course created successfully.");
    }

    @PostMapping("addCourses")
    public List<Course> addCourses(@RequestBody List<Course> courses) {
        List<Course> saveAll = courseRepository.saveAll(courses);
        return saveAll;
    }

    @DeleteMapping("deleteCourse")
    public ResponseEntity<?> deleteCourse(@RequestParam String courseName) {
        Course course = courseRepository.findByName(courseName);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found with name: " + courseName);
        }
        courseRepository.delete(course);
        return ResponseEntity.ok("Course deleted successfully");
    }
}
