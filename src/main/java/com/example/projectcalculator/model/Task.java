// Task.java (Entity/Model - pure data representation)
package com.example.projectcalculator.model;

import java.time.LocalDate;

public class Task {
    private Long id;
    private Long subProjectId;
    private String name;
    private String description;
    private LocalDate deadline;
    private Double estimatedHours;

    // Default constructor (required for ORM/JDBC)
    public Task() {}

    // All-args constructor (for convenience)
    public Task(Long id, Long subProjectId, String name,
                String description, LocalDate deadline, Double estimatedHours) {
        this.id = id;
        this.subProjectId = subProjectId;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.estimatedHours = estimatedHours;
    }

    // Getters and setters (no validation here)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSubProjectId() { return subProjectId; }
    public void setSubProjectId(Long subProjectId) { this.subProjectId = subProjectId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public Double getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Double estimatedHours) { this.estimatedHours = estimatedHours; }
}