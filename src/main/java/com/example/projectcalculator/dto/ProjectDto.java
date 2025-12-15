package com.example.projectcalculator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for creating or updating {@link com.example.projectcalculator.model.Project Project} entities.
 *
 * <p>This DTO is used to transfer project data between the presentation layer and the service layer,
 * typically through REST API endpoints or form submissions. It includes validation annotations
 * to ensure data integrity before processing.</p>
 *
 * <p><b>Validation Rules:</b></p>
 * <ul>
 *   <li><b>Name:</b> Required, cannot be blank, maximum 50 characters</li>
 *   <li><b>Description:</b> Optional, maximum 200 characters</li>
 *   <li><b>Deadline:</b> Optional, must be a valid ISO date format (YYYY-MM-DD)</li>
 * </ul>
 *
 * <p><b>Usage Examples:</b></p>
 * <p><b>1. Form Submission (Thymeleaf):</b></p>
 * <pre>
 * {@code
 * <form method="post" th:action="@{/projects}" th:object="${projectDto}">
 *   <input type="text" th:field="*{name}" placeholder="Project Name"/>
 *   <textarea th:field="*{description}" placeholder="Description"></textarea>
 *   <input type="date" th:field="*{deadline}"/>
 *   <button type="submit">Create Project</button>
 * </form>
 * }
 * </pre>
 *
 * <p><b>2. REST API Request:</b></p>
 * <pre>
 * {
 *   "name": "Website Redesign",
 *   "description": "Complete overhaul of the company website",
 *   "deadline": "2024-12-31"
 * }
 * </pre>
 *
 * <p><b>3. Programmatic Usage:</b></p>
 * <pre>
 * ProjectDto dto = new ProjectDto();
 * dto.setName("Mobile App Development");
 * dto.setDescription("Create a cross-platform mobile application");
 * dto.setDeadline(LocalDate.of(2024, 10, 31));
 * </pre>
 *
 * @author Arian
 * @version 1.0
 * @since 1.0
 * @see com.example.projectcalculator.model.Project
 * @see jakarta.validation.constraints.NotBlank
 * @see jakarta.validation.constraints.Size
 * @see org.springframework.format.annotation.DateTimeFormat
 */
public class ProjectDto {

    /**
     * The name of the project.
     *
     * <p><b>Constraints:</b></p>
     * <ul>
     *   <li>Cannot be null or empty (must contain at least one non-whitespace character)</li>
     *   <li>Maximum length: 50 characters</li>
     * </ul>
     *
     * @see jakarta.validation.constraints.NotBlank
     * @see jakarta.validation.constraints.Size
     */
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name cannot contain more than 50 characters")
    private String name;

    /**
     * Detailed description of the project.
     *
     * <p><b>Constraints:</b></p>
     * <ul>
     *   <li>Optional (may be null or empty)</li>
     *   <li>Maximum length: 200 characters</li>
     * </ul>
     *
     * @see jakarta.validation.constraints.Size
     */
    @Size(max = 200, message = "Description cannot contain more than 200 characters")
    private String description;

    /**
     * The target completion date for the project.
     *
     * <p><b>Format:</b> ISO date format (YYYY-MM-DD)</p>
     * <p><b>Constraints:</b></p>
     * <ul>
     *   <li>Optional (may be null)</li>
     *   <li>Must be a valid date</li>
     * </ul>
     *
     * <p><b>Example:</b> 2024-12-31</p>
     *
     * @see org.springframework.format.annotation.DateTimeFormat
     * @see java.time.LocalDate
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deadline;

    /**
     * Default constructor. Creates a new, uninitialized ProjectDto instance.
     *
     * <p>This constructor is typically used by:
     * <ul>
     *   <li>Spring MVC for form binding</li>
     *   <li>JSON deserializers (Jackson, Gson)</li>
     *   <li>When creating a DTO to be populated programmatically</li>
     * </ul></p>
     */
    public ProjectDto() {}

    /**
     * Returns the name of the project.
     *
     * @return the project name, or {@code null} if not set
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for the project.
     *
     * <p>The name will be validated according to the {@link NotBlank} and {@link Size} constraints
     * when this DTO is validated by Spring's validation framework.</p>
     *
     * @param name the project name
     * @see #name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the description of the project.
     *
     * @return the project description, or {@code null} if not set
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description for the project.
     *
     * <p>The description will be validated according to the {@link Size} constraint
     * when this DTO is validated by Spring's validation framework.</p>
     *
     * @param description the project description
     * @see #description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the deadline (target completion date) for the project.
     *
     * @return the project deadline, or {@code null} if not set
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Sets the deadline (target completion date) for the project.
     *
     * <p>The deadline should be in ISO date format (YYYY-MM-DD). When used with
     * Spring MVC form binding, dates in other formats will be automatically
     * converted if properly configured.</p>
     *
     * @param deadline the target completion date for the project
     * @see #deadline
     */
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}