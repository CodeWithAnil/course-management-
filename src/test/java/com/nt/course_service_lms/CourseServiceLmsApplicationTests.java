package com.nt.course_service_lms;

import com.nt.course_service_lms.config.TestS3Config;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestS3Config.class)
class CourseServiceLmsApplicationTests {

    @Test
    void contextLoads() {
    }

}
