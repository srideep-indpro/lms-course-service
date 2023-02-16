package com.vlabs.lms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlabs.lms.config.CourseEvent;
import com.vlabs.lms.config.CourseProducer;
import com.vlabs.lms.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vlabs.lms.model.Course;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("${lms.rest.base-url}")
public class CourseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseProducer.class);
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseProducer courseProducer;
    @Autowired
    private RestTemplate restTemplate;

    private final String UPDATE_STATUS = "COURSE_UPDATION_SUCCESSFUL";
    private final String UPDATE_MSG = "course updated.";
    private final String CREATE_STATUS_PENDING = "COURSE_CREATION_PENDING";
    private final String CREATE_MSG_PENDING = "course addition status is in pending state";
    private final String CREATE_STATUS_SUCCESS = "COURSE_CREATION_SUCCESS";
    private final String CREATE_MSG_SUCCESS = "course created.";
    private final String DELETE_STATUS_SUCCESS = "COURSE_DELETE_SUCCESS";
    private final String DELETE_MSG_SUCCESS = "course deleted.";

    @PostMapping("addOrUpdateCourse")
    public ResponseEntity<String> addorUpdateCourse(@RequestBody Course course,@RequestParam String loggedInUserName)  {
        String url = "http://localhost:8086/lms-user/getUserInfo?userName="+loggedInUserName;
        LOGGER.info("getUserInfo called with uri: "+url);
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode role = null;
        try{
            JsonNode root = mapper.readTree(forEntity.getBody());
            role = root.path("userRole");
        }catch(JsonProcessingException e){
            e.printStackTrace();
        }

        if(!role.toString().contains("ROLE_ADMIN")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User does not have access.");
        }

        Course courseByName = courseRepository.findByName(course.getName());
        Course savedOrUpdated = null;

        if (courseByName != null) {
            course.setId(courseByName.getId());
            savedOrUpdated = courseRepository.save(course);
            sendCourseEvent(UPDATE_STATUS,UPDATE_MSG,course);
            return ResponseEntity.status(HttpStatus.OK).body("Course updated successfully.");
        }

        sendCourseEvent(CREATE_STATUS_PENDING,CREATE_MSG_PENDING,course);
        savedOrUpdated = courseRepository.save(course);
        sendCourseEvent(CREATE_STATUS_SUCCESS,CREATE_MSG_SUCCESS,course);

        return ResponseEntity.status(HttpStatus.CREATED).body("Course created successfully.");
    }

    @DeleteMapping("deleteCourse")
    public ResponseEntity<?> deleteCourse(@RequestParam String courseName) {
        Course course = courseRepository.findByName(courseName);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found with name: " + courseName);
        }
        courseRepository.delete(course);
        sendCourseEvent(DELETE_STATUS_SUCCESS,DELETE_MSG_SUCCESS,course);
        return ResponseEntity.ok("Course deleted successfully");
    }

    private void sendCourseEvent(String status, String message, Course course) {
        CourseEvent courseEvent = new CourseEvent();
        courseEvent.setStatus(status);
        courseEvent.setMessage(message);
        courseEvent.setCourse(course);
        courseProducer.sendMessage(courseEvent);
    }
}
