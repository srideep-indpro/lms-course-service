package com.vlabs.lms.config;

import com.vlabs.lms.model.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseEvent {
    private String message;
    private String status;
    private Course course;
}
