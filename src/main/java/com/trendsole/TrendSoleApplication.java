package com.trendsole;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TrendSoleApplication - This is the MAIN class of our application.
 *
 * What it does:
 * - This is the entry point of the Spring Boot application.
 * - The @SpringBootApplication annotation does 3 things:
 *   1. @Configuration   → Marks this class as a configuration class
 *   2. @EnableAutoConfiguration → Spring Boot automatically configures things for us
 *   3. @ComponentScan   → Scans all packages under 'com.trendsole' to find controllers, services, etc.
 *
 * How to run:
 * - Right-click this file and click "Run" in your IDE, OR
 * - Use terminal: mvn spring-boot:run
 */
@SpringBootApplication
public class TrendSoleApplication {

    public static void main(String[] args) {
        // This line starts the entire Spring Boot application
        SpringApplication.run(TrendSoleApplication.class, args);
        System.out.println("✅ TrendSole Application Started Successfully!");
    }
}
