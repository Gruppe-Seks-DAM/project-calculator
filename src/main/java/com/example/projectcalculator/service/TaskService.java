package com.example.projectcalculator.service;

import com.example.projectcalculator.model.Task;
import com.example.projectcalculator.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Set;
import java.util.Optional;

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
     * #191 - Service: deleteTask(id)
     * Deletes a task by ID
     */
    @Transactional
    public boolean deleteTask(Long id) {
        // First, get the subproject ID for potential redirect
        Optional<Long> subProjectIdOpt = taskRepository.getSubProjectIdForTask(id);

        if (subProjectIdOpt.isEmpty()) {
            return false; // Task doesn't exist
        }

        // Delete the task
        return taskRepository.deleteById(id);
    }

    /**
     * Get subproject ID for a task
     */
    public Optional<Long> getSubProjectIdForTask(Long taskId) {
        return taskRepository.getSubProjectIdForTask(taskId);
    }

    // Existing methods (from previous implementation)
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
                System.out.println("Warning: estimated_hours column not found in TASK table.");
                // Alternative method would be needed here
                throw e;
            }
            throw e;
        }
    }

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
    }

    public List<Task> getTasksBySubProjectId(Long subProjectId) {
        return taskRepository.findBySubProjectId(subProjectId);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
}