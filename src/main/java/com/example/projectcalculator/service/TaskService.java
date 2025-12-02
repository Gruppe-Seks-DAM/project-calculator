package com.example.projectcalculator.service;

import com.example.projectcalculator.model.Task;
import com.example.projectcalculator.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final Validator validator;

    @Autowired
    public TaskService(TaskRepository taskRepository, Validator validator) {
        this.taskRepository = taskRepository;
        this.validator = validator;
    }

    /**
     * #179 - Service: createTask(Task)
     * Creates a new task with validation
     */
    @Transactional
    public Task createTask(Task task) {
        // #181 - Add validation (estimatedHours > 0)
        validateTask(task);

        // Check if subproject exists (repository will handle this, but we can add extra validation)
        if (task.getSubProjectId() == null) {
            throw new IllegalArgumentException("Task must be linked to a subproject");
        }

        // Create the task in the database
        // Choose the appropriate method based on whether estimated_hours column exists
        try {
            return taskRepository.create(task);
        } catch (Exception e) {
            // If the error is due to missing estimated_hours column, use the alternative method
            if (e.getMessage().contains("estimated_hours") || e.getMessage().contains("Unknown column")) {
                // Log warning about schema mismatch
                System.out.println("Warning: estimated_hours column not found in TASK table. Creating task without estimated hours.");
                return taskRepository.createWithoutEstimatedHours(task);
            }
            throw e;
        }
    }

    /**
     * #181 - Validation logic
     */
    private void validateTask(Task task) {
        // Use JSR-303 validation
        Set<ConstraintViolation<Task>> violations = validator.validate(task);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Task> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            throw new IllegalArgumentException("Validation failed: " + sb.toString());
        }

        // Additional business validation
        if (task.getEstimatedHours() != null && task.getEstimatedHours() <= 0) {
            throw new IllegalArgumentException("Estimated hours must be greater than 0");
        }

        // Validate deadline is not in the past (optional business rule)
        if (task.getDeadline() != null && task.getDeadline().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Deadline cannot be in the past");
        }
    }

    /**
     * Get tasks by subproject ID
     */
    public List<Task> getTasksBySubProjectId(Long subProjectId) {
        return taskRepository.findBySubProjectId(subProjectId);
    }

    /**
     * Get task by ID
     */
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
}