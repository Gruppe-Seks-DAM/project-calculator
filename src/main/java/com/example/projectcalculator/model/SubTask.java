package com.example.projectcalculator.model;// SubTask.java
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public class SubTask {
    private Long id;
    private Long taskId;

    @NotBlank(message = "Sub-task name is required")
    @Size(min = 3, max = 50, message = "Sub-task name must be between 3 and 50 characters")
    private String name;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;

    @NotNull(message = "Deadline is required")
    private LocalDate deadline;

    @NotNull(message = "Estimated hours are required")
    @Positive(message = "Estimated hours must be positive")
    private Double estimatedHours;

    // constructors, getters, setters
    public SubTask() {}

    public SubTask(Long id, Long taskId, String name, String description, LocalDate deadline, Double estimatedHours) {
        this.id = id;
        this.taskId = taskId;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.estimatedHours = estimatedHours;
    }

    // getters and setters for all fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public Double getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Double estimatedHours) { this.estimatedHours = estimatedHours; }
}