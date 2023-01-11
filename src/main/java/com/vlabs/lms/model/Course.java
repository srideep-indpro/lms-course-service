package com.vlabs.lms.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Document("COURSES")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Course {

    @Id
    private String id;
    @NotNull
    @Size(min = 5)
    private String name;
    @NotNull
    private int duration;
    @NotNull
    @Size(min = 10)
    private String description;
    @NotNull
    private String technology;
    @NotNull
    private String launchURL;

    public Course(String name, int duration, String description, String technology, String launchURL) {
        super();
        this.name = name;
        this.duration = duration;
        this.description = description;
        this.technology = technology;
        this.launchURL = launchURL;
    }

}
