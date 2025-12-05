package com.example.projectcalculator.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class Task {
    private Long id;

    @NotNull(message = "Subproject ID is required")
    private Long subProjectId;

    @NotNull(message = "Task name is required")
    @Size(min = 1, max = 50, message = "Task name must be between 1 and 50 characters")
    private String name;

    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    private LocalDate deadline;

    @NotNull(message = "Estimated hours is required")
    @Min(value = 0, message = "Estimated hours must be greater than 0") // Changed to 0 or greater
    private Double estimatedHours; // Added as per user story

    // Constructors
    public Task() {}

    public Task(Long id, Long subProjectId, String name, String description,
                LocalDate deadline, Double estimatedHours) {
        this.id = id;
        this.subProjectId = subProjectId;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.estimatedHours = estimatedHours;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubProjectId() {
        return subProjectId;
    }

    public void setSubProjectId(Long subProjectId) {
        this.subProjectId = subProjectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }
}