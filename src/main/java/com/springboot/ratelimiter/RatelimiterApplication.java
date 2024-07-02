package com.springboot.ratelimiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class named {@link RatelimiterApplication} to start the Rate limiter application.
 */
@SpringBootApplication
public class RatelimiterApplication {

	/**
	 * Main method to start the Spring Boot application.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(RatelimiterApplication.class, args);
	}

}
