package com.example.projectcalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Project Calculator Spring Boot application.
 *
 * <p>This class serves as the primary configuration class and application bootstrap.
 * The {@link SpringBootApplication} annotation enables autoconfiguration, component scanning,
 * and defines this as a configuration class for the Spring Boot application.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     ProjectCalculatorApplication.main(new String[] {});
 * </pre>
 *
 * @author Arian
 * @version 1.0
 * @since 1.0
 * @see SpringBootApplication
 * @see SpringApplication
 */
@SpringBootApplication
public class ProjectCalculatorApplication {

	/**
	 * Main method which serves as the entry point for the Spring Boot application.
	 *
	 * <p>This method delegates to Spring Boot's {@link SpringApplication#run(Class, String...)}
	 * method to bootstrap the application context and start the embedded web server.</p>
	 *
	 * @param args command-line arguments passed to the application (can be used to configure
	 *             Spring Boot properties, profiles, etc.)
	 *
	 * @see SpringApplication#run(Class, String...)
	 */
	public static void main(String[] args) {
		SpringApplication.run(ProjectCalculatorApplication.class, args);
	}
}