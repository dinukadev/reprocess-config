package org.dimashup.reprocess.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Application starter
 */
@SpringBootApplication(scanBasePackages = "org.dimashup.reprocess")
public class SampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
