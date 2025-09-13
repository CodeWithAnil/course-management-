package com.nt.course_service_lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Course Service LMS Spring Boot application.
 */
@SpringBootApplication
public final class CourseServiceLmsApplication {
    /**
     * Private constructor to prevent instantiation.
     * This class is meant to be run, not instantiated.
     */
    private CourseServiceLmsApplication() {

    }

    /**
     * Main method used to launch the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(final String[] args) {
        SpringApplication.run(CourseServiceLmsApplication.class, args);
    }
}
