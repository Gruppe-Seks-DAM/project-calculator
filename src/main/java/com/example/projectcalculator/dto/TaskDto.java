package com.example.projectcalculator.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for creating, updating, or transferring {@link com.example.projectcalculator.model.Task Task} entities.
 *
 * <p>This DTO represents the smallest unit of work in the project hierarchy, containing
 * specific work items that need to be completed within a {@link com.example.projectcalculator.model.SubProject SubProject}.
 * It includes comprehensive validation to ensure data integrity and business rules.</p>
 *
 * <p><b>Architecture Context:</b>
 * TaskDto represents individual work items within a sub-project. Unlike {@link ProjectDto} and {@link SubProjectDto},
 * tasks have more stringent validation for estimated hours and always require a parent sub-project association.</p>
 *
 * <p><b>Validation Rules:</b></p>
 * <table border="1" style="border-collapse: collapse; width: 100%;">
 *   <tr><th>Field</th><th>Required</th><th>Constraints</th><th>Notes</th></tr>
 *   <tr>
 *     <td>id</td><td>No</td><td>None (auto-generated)</td>
 *     <td>Only present for updates, not for creation</td>
 *   </tr>
 *   <tr>
 *     <td>subProjectId</td><td>Yes</td><td>Must not be null</td>
 *     <td>Must reference an existing sub-project</td>
 *   </tr>
 *   <tr>
 *     <td>name</td><td>Yes</td><td>1-50 characters</td>
 *     <td>Must contain at least one character</td>
 *   </tr>
 *   <tr>
 *     <td>description</td><td>No</td><td>Max 200 characters</td>
 *     <td>Optional detailed description</td>
 *   </tr>
 *   <tr>
 *     <td>deadline</td><td>No</td><td>Valid date</td>
 *     <td>Should be within parent sub-project's timeline</td>
 *   </tr>
 *   <tr>
 *     <td>estimatedHours</td><td>Yes</td><td>≥ 0</td>
 *     <td>Required for effort estimation and planning</td>
 *   </tr>
 * </table>
 *
 * <p><b>Usage Examples:</b></p>
 * <p><b>1. Form Submission (Thymeleaf) within a Sub-Project:</b></p>
 * <pre>
 * {@code
 * <!-- Form to add a task to an existing sub-project -->
 * <form method="post" th:action="@{/subprojects/{subProjectId}/tasks(subProjectId=${subProject.id})}"
 *       th:object="${taskDto}">
 *   <input type="hidden" th:field="*{subProjectId}" />
 *   <input type="text" th:field="*{name}" placeholder="Task Name" required/>
 *   <textarea th:field="*{description}" placeholder="Task Description"></textarea>
 *   <input type="date" th:field="*{deadline}"/>
 *   <input type="number" th:field="*{estimatedHours}" step="0.5" min="0" required/>
 *   <button type="submit">Add Task</button>
 * </form>
 * }
 * </pre>
 *
 * <p><b>2. REST API Request for creating a task:</b></p>
 * <pre>
 * POST /api/subprojects/456/tasks
 * Content-Type: application/json
 *
 * {
 *   "subProjectId": 456,
 *   "name": "Implement Login API",
 *   "description": "Create REST endpoints for user authentication",
 *   "deadline": "2024-06-15",
 *   "estimatedHours": 8.0
 * }
 * </pre>
 *
 * <p><b>3. REST API Request for updating a task:</b></p>
 * <pre>
 * PUT /api/tasks/789
 * Content-Type: application/json
 *
 * {
 *   "id": 789,
 *   "subProjectId": 456,
 *   "name": "Implement Login API v2",
 *   "description": "Create REST endpoints with enhanced security",
 *   "deadline": "2024-06-20",
 *   "estimatedHours": 12.0
 * }
 * </pre>
 *
 * <p><b>4. Programmatic Usage:</b></p>
 * <pre>
 * // For creation (ID is null)
 * TaskDto newTask = new TaskDto();
 * newTask.setSubProjectId(456L);
 * newTask.setName("Write Unit Tests");
 * newTask.setDescription("Create comprehensive test suite for the authentication module");
 * newTask.setDeadline(LocalDate.of(2024, 6, 25));
 * newTask.setEstimatedHours(6.5);
 *
 * // For update (ID is populated)
 * TaskDto existingTask = new TaskDto(789L, 456L, "Updated Task Name",
 *                                    "Updated description", LocalDate.now().plusDays(7), 10.0);
 * </pre>
 *
 * @author Daniel
 * @version 1.0
 * @since 1.0
 * @see com.example.projectcalculator.model.Task
 * @see com.example.projectcalculator.model.SubProject
 * @see ProjectDto
 * @see SubProjectDto
 * @see jakarta.validation.constraints.NotNull
 * @see jakarta.validation.constraints.Size
 * @see jakarta.validation.constraints.Min
 */
public class TaskDto {

    /**
     * Unique identifier for the task.
     *
     * <p><b>Usage Notes:</b></p>
     * <ul>
     *   <li><b>For creation:</b> Should be {@code null} (database will generate)</li>
     *   <li><b>For updates:</b> Must contain the ID of the existing task</li>
     *   <li><b>For retrieval:</b> Populated when reading from the database</li>
     * </ul>
     */
    private Long id;

    /**
     * Identifier of the parent sub-project to which this task belongs.
     *
     * <p><b>Constraints:</b></p>
     * <ul>
     *   <li>Cannot be {@code null}</li>
     *   <li>Must reference an existing sub-project in the database</li>
     * </ul>
     *
     * <p>This field establishes the foreign key relationship between tasks and sub-projects.
     * A task cannot exist without a parent sub-project.</p>
     *
     * @see jakarta.validation.constraints.NotNull
     */
    @NotNull(message = "Subproject ID is required")
    private Long subProjectId;

    /**
     * The name of the task.
     *
     * <p><b>Constraints:</b></p>
     * <ul>
     *   <li>Cannot be {@code null}</li>
     *   <li>Must be between 1 and 50 characters inclusive</li>
     * </ul>
     *
     * <p><b>Example:</b> "Implement User Registration Form"</p>
     *
     * @see jakarta.validation.constraints.NotNull
     * @see jakarta.validation.constraints.Size
     */
    @NotNull(message = "Task name is required")
    @Size(min = 1, max = 50, message = "Task name must be between 1 and 50 characters")
    private String name;

    /**
     * Detailed description of the task requirements and deliverables.
     *
     * <p><b>Constraints:</b></p>
     * <ul>
     *   <li>Optional (may be {@code null} or empty)</li>
     *   <li>Maximum length: 200 characters</li>
     * </ul>
     *
     * <p><b>Example:</b> "Create HTML/CSS form with validation, connect to backend API, implement error handling"</p>
     *
     * @see jakarta.validation.constraints.Size
     */
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    /**
     * The target completion date for this specific task.
     *
     * <p><b>Constraints:</b></p>
     * <ul>
     *   <li>Optional (may be {@code null})</li>
     *   <li>Must be a valid date if provided</li>
     *   <li>Business logic may require this to be within the parent sub-project's deadline</li>
     * </ul>
     *
     * <p><b>Format:</b> Typically YYYY-MM-DD in JSON/API contexts</p>
     *
     * @see java.time.LocalDate
     */
    private LocalDate deadline;

    /**
     * The estimated number of hours required to complete this task.
     *
     * <p><b>Constraints:</b></p>
     * <ul>
     *   <li>Cannot be {@code null}</li>
     *   <li>Must be 0 or greater (0 indicates no time estimate or placeholder)</li>
     *   <li>Typically expressed in hours (e.g., 2.5 for 2 hours 30 minutes)</li>
     * </ul>
     *
     * <p><b>Business Logic:</b> This value is used for project planning, resource allocation,
     * and calculating total project estimates.</p>
     *
     * @see jakarta.validation.constraints.NotNull
     * @see jakarta.validation.constraints.Min
     */
    @NotNull(message = "Estimated hours is required")
    @Min(value = 0, message = "Estimated hours must be 0 or greater")
    private Double estimatedHours;

    /**
     * Default constructor. Creates a new, uninitialized TaskDto instance.
     *
     * <p>Use this constructor for:
     * <ul>
     *   <li>Spring MVC form binding</li>
     *   <li>JSON deserialization in REST APIs</li>
     *   <li>Programmatic creation where fields will be set via setters</li>
     * </ul></p>
     */
    public TaskDto() {}

    /**
     * Creates a fully initialized TaskDto instance with all fields.
     *
     * <p>Use this constructor when you have all field values available, such as:
     * <ul>
     *   <li>Converting a {@link com.example.projectcalculator.model.Task} entity to a DTO</li>
     *   <li>Creating test data</li>
     *   <li>Programmatically creating a complete task representation</li>
     * </ul></p>
     *
     * @param id the unique identifier for the task (can be {@code null} for new tasks)
     * @param subProjectId the identifier of the parent sub-project (must not be {@code null})
     * @param name the name of the task (must not be {@code null} or empty)
     * @param description a detailed description of the task
     * @param deadline the target completion date for the task
     * @param estimatedHours the estimated hours required to complete the task (must not be {@code null} and ≥ 0)
     *
     * @throws IllegalArgumentException if {@code name} is {@code null} or empty,
     *                                  {@code subProjectId} is {@code null},
     *                                  {@code estimatedHours} is {@code null} or negative
     */
    public TaskDto(Long id, Long subProjectId, String name, String description,
                   LocalDate deadline, Double estimatedHours) {
        this.id = id;
        this.subProjectId = subProjectId;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.estimatedHours = estimatedHours;
    }

    /**
     * Returns the unique identifier of this task.
     *
     * @return the task's unique identifier, or {@code null} if not set
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this task.
     *
     * @param id the unique identifier to assign to this task
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the identifier of the parent sub-project.
     *
     * @return the parent sub-project identifier, or {@code null} if not set
     */
    public Long getSubProjectId() {
        return subProjectId;
    }

    /**
     * Sets the parent sub-project identifier for this task.
     *
     * <p><b>Validation:</b> The value should reference an existing sub-project.
     * This is typically validated at the service layer.</p>
     *
     * @param subProjectId the identifier of the parent sub-project
     */
    public void setSubProjectId(Long subProjectId) {
        this.subProjectId = subProjectId;
    }

    /**
     * Returns the name of this task.
     *
     * @return the task name, or {@code null} if not set
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for this task.
     *
     * <p>The name will be validated according to the {@link NotNull} and {@link Size} constraints
     * when this DTO is validated by Spring's validation framework.</p>
     *
     * @param name the task name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the description of this task.
     *
     * @return the task description, or {@code null} if not set
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description for this task.
     *
     * <p>The description will be validated according to the {@link Size} constraint
     * when this DTO is validated by Spring's validation framework.</p>
     *
     * @param description a detailed description of the task
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the deadline (target completion date) for this task.
     *
     * @return the task deadline, or {@code null} if not set
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Sets the deadline (target completion date) for this task.
     *
     * @param deadline the target completion date for the task
     */
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    /**
     * Returns the estimated hours required to complete this task.
     *
     * @return the estimated hours, or {@code null} if not set
     */
    public Double getEstimatedHours() {
        return estimatedHours;
    }

    /**
     * Sets the estimated hours required to complete this task.
     *
     * <p>The estimated hours will be validated according to the {@link NotNull} and {@link Min} constraints
     * when this DTO is validated by Spring's validation framework.</p>
     *
     * <p><b>Note:</b> This value should be expressed in hours and can include decimal values
     * (e.g., 1.5 for 1 hour 30 minutes).</p>
     *
     * @param estimatedHours the estimated hours required to complete the task
     */
    public void setEstimatedHours(Double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }
}