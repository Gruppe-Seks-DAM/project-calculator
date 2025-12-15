package com.example.projectcalculator.service;

import com.example.projectcalculator.model.Task;
import com.example.projectcalculator.repository.TaskRepository;

import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;

/**
 * Service layer for managing {@link Task} entities in the Project Calculator application.
 *
 * <p>This service provides comprehensive business operations for managing tasks, which are
 * individual work items within {@link com.example.projectcalculator.model.SubProject SubProject}
 * entities. It includes validation, error handling, and fallback logic for schema variations.</p>
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li>Comprehensive validation using Jakarta Bean Validation and custom business rules</li>
 *   <li>Transaction management for data consistency</li>
 *   <li>Fallback mechanism for database schema variations (estimated_hours column)</li>
 *   <li>Robust error handling and informative exception messages</li>
 *   <li>Business rule enforcement (deadlines, estimated hours, etc.)</li>
 * </ul>
 *
 * <p><b>Architecture Context:</b></p>
 * <p>TaskService is the most complex service in the hierarchy because tasks have the most
 * stringent validation rules and must integrate with both {@link SubProjectService} and
 * {@link ProjectService} for complete project management.</p>
 *
 * <p><b>Database Schema Fallback:</b></p>
 * <p>This service includes a fallback mechanism to handle missing database columns
 * (specifically {@code estimated_hours}). If the column doesn't exist, tasks are created
 * without estimated hours. This allows for backward compatibility and schema evolution.</p>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>
 * // In a controller:
 * &#64;Autowired
 * private TaskService taskService;
 *
 * // Create a new task
 * Task task = new Task();
 * task.setSubProjectId(456L);
 * task.setName("Implement Login API");
 * task.setDescription("Create authentication endpoints");
 * task.setDeadline(LocalDate.now().plusDays(14));
 * task.setEstimatedHours(8.5);
 * Task createdTask = taskService.createTask(task);
 *
 * // Get task by ID
 * Optional<Task> task = taskService.getTaskById(123L);
 *
 * // Get all tasks for a sub-project
 * List<Task> tasks = taskService.getTasksBySubProjectId(456L);
 *
 * // Update a task
 * task.setEstimatedHours(10.0);
 * boolean updated = taskService.updateTask(task);
 *
 * // Delete a task
 * boolean deleted = taskService.deleteTask(123L);
 * </pre>
 *
 * @author Daniel
 * @version 1.0
 * @since 1.0
 * @see Task
 * @see TaskRepository
 * @see SubProjectService
 * @see Service
 * @see Transactional
 * @see jakarta.validation.Validator
 */
@Service
public class TaskService {

    /** Repository for performing data access operations on Task entities. */
    private final TaskRepository taskRepository;

    /** Jakarta Bean Validator for performing entity validation. */
    private final Validator validator;

    /**
     * Constructs a new TaskService with the specified dependencies.
     *
     * <p>Both dependencies are required and automatically injected by Spring.
     * The Validator is typically provided by Spring's LocalValidatorFactoryBean.</p>
     *
     * @param taskRepository the TaskRepository to use for data access operations
     * @param validator the Jakarta Validator for entity validation
     * @throws IllegalArgumentException if {@code taskRepository} or {@code validator} is {@code null}
     */
    @Autowired
    public TaskService(TaskRepository taskRepository, Validator validator) {
        this.taskRepository = taskRepository;
        this.validator = validator;
    }

    /**
     * Deletes a task by its ID within a transactional context.
     *
     * <p>This method performs a safe deletion by first verifying that the task exists
     * and retrieving its parent sub-project ID. The transaction ensures data consistency
     * if additional cascade operations are added in the future.</p>
     *
     * <p><b>Transaction Management:</b></p>
     * <p>The {@link Transactional} annotation ensures that the operation occurs within
     * a transaction. If an exception is thrown, the transaction is rolled back.</p>
     *
     * <p><b>Validation Steps:</b></p>
     * <ol>
     *   <li>Retrieve the sub-project ID for the task (verifies task exists)</li>
     *   <li>If task doesn't exist, return {@code false}</li>
     *   <li>Delete the task via repository</li>
     * </ol>
     *
     * @param id the ID of the task to delete
     * @return {@code true} if the task was successfully deleted, {@code false} if the task doesn't exist
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     * @throws org.springframework.transaction.TransactionException if transaction management fails
     *
     * @see Transactional
     * @see TaskRepository#getSubProjectIdForTask(Long)
     * @see TaskRepository#deleteById(Long)
     */
    @Transactional
    public boolean deleteTask(Long id) {

        Optional<Long> subProjectIdOpt = taskRepository.getSubProjectIdForTask(id);

        if (subProjectIdOpt.isEmpty()) {
            return false;
        }


        return taskRepository.deleteById(id);
    }

    /**
     * Retrieves the sub-project ID associated with a specific task.
     *
     * <p>This method is useful for navigation and permission checking when you need
     * to know which sub-project (and by extension, which project) a task belongs to.</p>
     *
     * @param taskId the ID of the task
     * @return an {@link Optional} containing the sub-project ID, or {@link Optional#empty()}
     *         if no task with the given ID exists
     * @throws IllegalArgumentException if {@code taskId} is {@code null}
     *
     * @see TaskRepository#getSubProjectIdForTask(Long)
     */
    public Optional<Long> getSubProjectIdForTask(Long taskId) {
        return taskRepository.getSubProjectIdForTask(taskId);
    }

    /**
     * Finds a task by its unique identifier.
     *
     * <p>Alias for {@link #getTaskById(Long)}. This method provides consistency with
     * repository naming conventions.</p>
     *
     * @param id the ID of the task to find
     * @return an {@link Optional} containing the found task, or {@link Optional#empty()}
     *         if no task with the given ID exists
     * @throws IllegalArgumentException if {@code id} is {@code null}
     *
     * @see #getTaskById(Long)
     * @see TaskRepository#findById(Long)
     */
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    /**
     * Updates an existing task in the database.
     *
     * <p><b>Important:</b> This method does not validate the task before updating.
     * Consider using {@link #validateTask(Task)} before calling this method or
     * refactoring to include validation.</p>
     *
     * @param task the task with updated values (must not be {@code null} and must have a valid ID)
     * @return {@code true} if the task was successfully updated, {@code false} otherwise
     * @throws IllegalArgumentException if {@code task} is {@code null} or has invalid data
     *
     * @see TaskRepository#update(Task)
     */
    public boolean updateTask(Task task) {
        return taskRepository.update(task);
    }

    /**
     * Creates a new task with comprehensive validation and fallback handling.
     *
     * <p>This method performs the following steps within a transaction:</p>
     * <ol>
     *   <li>Validates the task using {@link #validateTask(Task)}</li>
     *   <li>Ensures the task is linked to a sub-project</li>
     *   <li>Attempts to create the task with estimated hours</li>
     *   <li>If the database lacks the {@code estimated_hours} column, falls back to
     *       creating without estimated hours</li>
     * </ol>
     *
     * <p><b>Database Schema Fallback:</b></p>
     * <p>The method includes a fallback mechanism that detects when the
     * {@code estimated_hours} column is missing from the database table. This allows
     * the application to work with different database schemas and provides a migration
     * path for adding the column later.</p>
     *
     * <p><b>Transaction Management:</b></p>
     * <p>The {@link Transactional} annotation ensures that if an exception occurs
     * during task creation, no partial data is persisted.</p>
     *
     * @param task the task to create (must not be {@code null})
     * @return the created task with its generated ID set
     * @throws IllegalArgumentException if validation fails or required data is missing
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     * @throws org.springframework.transaction.TransactionException if transaction management fails
     *
     * @see Transactional
     * @see #validateTask(Task)
     * @see TaskRepository#create(Task)
     * @see TaskRepository#createWithoutEstimatedHours(Task)
     */
    @Transactional
    public Task createTask(Task task) {

        validateTask(task);

        if (task.getSubProjectId() == null) {
            throw new IllegalArgumentException("Task must be linked to a subproject");
        }

        try {
            return taskRepository.create(task);
        } catch (Exception e) {

            if (e.getMessage().contains("estimated_hours") || e.getMessage().contains("Unknown column")) {

                System.out.println("Warning: estimated_hours column not found in TASK table. Creating task without estimated hours.");
                return taskRepository.createWithoutEstimatedHours(task);
            }
            throw e;
        }
    }

    /**
     * Validates a task entity using both Jakarta Bean Validation and custom business rules.
     *
     * <p>This method performs two levels of validation:</p>
     * <ol>
     *   <li><b>Field-level validation:</b> Uses Jakarta Bean Validation annotations
     *       on the Task entity class (e.g., @NotNull, @Size)</li>
     *   <li><b>Business rule validation:</b> Custom rules that aren't expressible
     *       with standard annotations</li>
     * </ol>
     *
     * <p><b>Custom Business Rules:</b></p>
     * <ul>
     *   <li>Estimated hours must be greater than 0 (if specified)</li>
     *   <li>Deadline cannot be in the past</li>
     * </ul>
     *
     * <p><b>Note:</b> Consider moving the business rule validation to a dedicated
     * validator class for better separation of concerns.</p>
     *
     * @param task the task to validate (must not be {@code null})
     * @throws IllegalArgumentException if validation fails, with a detailed message
     *         listing all validation errors
     */
    private void validateTask(Task task) {

        Set<ConstraintViolation<Task>> violations = validator.validate(task);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Task> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            throw new IllegalArgumentException("Validation failed: " + sb.toString());
        }


        if (task.getEstimatedHours() != null && task.getEstimatedHours() <= 0) {
            throw new IllegalArgumentException("Estimated hours must be greater than 0");
        }


        if (task.getDeadline() != null && task.getDeadline().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Deadline cannot be in the past");
        }
    }

    /**
     * Retrieves all tasks associated with a specific sub-project.
     *
     * <p>Tasks are returned in order of their deadline (ascending). Tasks without
     * a deadline appear first in the list.</p>
     *
     * @param subProjectId the ID of the sub-project
     * @return a list of tasks belonging to the specified sub-project, ordered by deadline;
     *         returns an empty list if no tasks exist for the sub-project
     * @throws IllegalArgumentException if {@code subProjectId} is {@code null}
     *
     * @see TaskRepository#findBySubProjectId(Long)
     */
    public List<Task> getTasksBySubProjectId(Long subProjectId) {
        return taskRepository.findBySubProjectId(subProjectId);
    }

    /**
     * Retrieves a task by its unique identifier.
     *
     * <p>This is the primary method for fetching a single task. It returns an
     * {@link Optional} to safely handle non-existent tasks without throwing exceptions.</p>
     *
     * @param id the ID of the task to retrieve
     * @return an {@link Optional} containing the found task, or {@link Optional#empty()}
     *         if no task with the given ID exists
     * @throws IllegalArgumentException if {@code id} is {@code null}
     *
     * @see TaskRepository#findById(Long)
     */
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
}