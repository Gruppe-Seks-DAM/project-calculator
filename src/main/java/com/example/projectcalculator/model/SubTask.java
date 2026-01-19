package com.example.projectcalculator.model;

import java.time.LocalDate;

public class SubTask {

    private Long taskId;
    private Long id;
    private String name;
    private String description;
    private LocalDate deadline;
    private Double estimatedHours;

    public SubTask() {
    }

    public SubTask(Long taskId, Long id, String name, String description, LocalDate deadline, Double estimatedHours) {
        this.taskId = taskId;
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.estimatedHours = estimatedHours;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
