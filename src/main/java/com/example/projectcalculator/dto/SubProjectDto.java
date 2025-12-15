package com.example.projectcalculator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for creating or updating {@link com.example.projectcalculator.model.SubProject SubProject} entities.
 *
 * <p>This DTO is used to transfer sub-project data between the presentation layer and the service layer,
 * typically for operations within a parent {@link com.example.projectcalculator.model.Project Project}.
 * It includes validation annotations to ensure data integrity before processing.</p>
 *
 * <p><b>Architecture Context:</b>
 * SubProjectDto represents a component or phase within a larger project. Unlike {@link ProjectDto},
 * which represents the main project entity, this DTO is used for managing sub-divisions of work
 * that help organize complex projects into manageable sections.</p>
 *
 * <p><b>Validation Rules:</b></p>
 * <ul>
 *   <li><b>Name:</b> Required, cannot be blank, maximum 50 characters</li>
 *   <li><b>Description:</b> Optional, maximum 200 characters</li>
 *   <li><b>Deadline:</b> Optional, must be a valid ISO date format (YYYY-MM-DD)</li>
 * </ul>
 *
 * <p><b>Relationship to Project:</b></p>
 * <p>When using this DTO, the parent project ID is typically provided separately (e.g., as a path variable
 * or in a parent form field) since sub-projects cannot exist without a parent project.</p>
 *
 * <p><b>Usage Examples:</b></p>
 * <p><b>1. Form Submission (Thymeleaf) within a Project:</b></p>
 * <pre>
 * {@code
 * <!-- Form to add a sub-project to an existing project -->
 * <form method="post" th:action="@{/projects/{projectId}/subprojects(projectId=${project.id})}"
 *       th:object="${subProjectDto}">
 *   <input type="text" th:field="*{name}" placeholder="Sub-Project Name"/>
 *   <textarea th:field="*{description}" placeholder="Description"></textarea>
 *   <input type="date" th:field="*{deadline}"/>
 *   <button type="submit">Add Sub-Project</button>
 * </form>
 * }
 * </pre>
 *
 * <p><b>2. REST API Request for creating a sub-project:</b></p>
 * <pre>
 * POST /api/projects/123/subprojects
 * Content-Type: application/json
 *
 * {
 *   "name": "UI/UX Design Phase",
 *   "description": "Complete user interface and experience design",
 *   "deadline": "2024-08-15"
 * }
 * </pre>
 *
 * <p><b>3. Programmatic Usage:</b></p>
 * <pre>
 * SubProjectDto dto = new SubProjectDto();
 * dto.setName("Backend Development");
 * dto.setDescription("Implement server-side logic and APIs");
 * dto.setDeadline(LocalDate.of(2024, 9, 30));
 *
 * // The parent project ID (e.g., 123) would be handled separately
 * </pre>
 *
 * @author Magnus
 * @version 1.0
 * @since 1.0
 * @see com.example.projectcalculator.model.SubProject
 * @see com.example.projectcalculator.model.Project
 * @see ProjectDto
 * @see jakarta.validation.constraints.NotBlank
 * @see jakarta.validation.constraints.Size
 * @see org.springframework.format.annotation.DateTimeFormat
 */
public class SubProjectDto {

    /**
     * The name of the sub-project.
     *
     * <p><b>Constraints:</b></p>
     * <ul>
     *   <li>Cannot be null or empty (must contain at least one non-whitespace character)</li>
     *   <li>Maximum length: 50 characters</li>
     * </ul>
     *
     * <p><b>Example:</b> "User Authentication Module"</p>
     *
     * @see jakarta.validation.constraints.NotBlank
     * @see jakarta.validation.constraints.Size
     */
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name cannot contain more than 50 characters")
    private String name;

    /**
     * Detailed description of the sub-project objectives and scope.
     *
     * <p><b>Constraints:</b></p>
     * <ul>
     *   <li>Optional (may be null or empty)</li>
     *   <li>Maximum length: 200 characters</li>
     * </ul>
     *
     * <p><b>Example:</b> "Implement user registration, login, password reset, and OAuth2 integration"</p>
     *
     * @see jakarta.validation.constraints.Size
     */
    @Size(max = 200, message = "Description cannot contain more than 200 characters")
    private String description;

    /**
     * The target completion date for this sub-project component.
     *
     * <p><b>Format:</b> ISO date format (YYYY-MM-DD)</p>
     * <p><b>Constraints:</b></p>
     * <ul>
     *   <li>Optional (may be null)</li>
     *   <li>Must be a valid date</li>
     *   <li>Typically should not exceed the parent project's deadline (business logic validation)</li>
     * </ul>
     *
     * <p><b>Example:</b> 2024-07-31</p>
     *
     * @see org.springframework.format.annotation.DateTimeFormat
     * @see java.time.LocalDate
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deadline;

    /**
     * Default constructor. Creates a new, uninitialized SubProjectDto instance.
     *
     * <p>This constructor is typically used by:
     * <ul>
     *   <li>Spring MVC for form binding in web controllers</li>
     *   <li>JSON deserializers (Jackson, Gson) in REST APIs</li>
     *   <li>When creating a DTO to be populated programmatically</li>
     * </ul></p>
     */
    public SubProjectDto() {}

    /**
     * Returns the name of the sub-project.
     *
     * @return the sub-project name, or {@code null} if not set
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for the sub-project.
     *
     * <p>The name will be validated according to the {@link NotBlank} and {@link Size} constraints
     * when this DTO is validated by Spring's validation framework.</p>
     *
     * @param name the sub-project name
     * @see #name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the description of the sub-project.
     *
     * @return the sub-project description, or {@code null} if not set
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description for the sub-project.
     *
     * <p>The description will be validated according to the {@link Size} constraint
     * when this DTO is validated by Spring's validation framework.</p>
     *
     * @param description the sub-project description
     * @see #description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the deadline (target completion date) for the sub-project.
     *
     * @return the sub-project deadline, or {@code null} if not set
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Sets the deadline (target completion date) for the sub-project.
     *
     * <p>The deadline should be in ISO date format (YYYY-MM-DD). When used with
     * Spring MVC form binding, dates in other formats will be automatically
     * converted if properly configured.</p>
     *
     * <p><b>Note:</b> Business logic may require that this deadline falls within
     * the parent project's timeline. Such validation typically occurs at the service layer.</p>
     *
     * @param deadline the target completion date for the sub-project
     * @see #deadline
     */
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}