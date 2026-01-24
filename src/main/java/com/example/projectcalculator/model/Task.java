package com.example.projectcalculator.model;

import java.time.LocalDate;
import java.util.List;

public class Task {

    private Long subProjectId;
    private Long id;
    private String name;
    private String description;
    private LocalDate deadline;

    private Double estimatedHours;

    private List<SubTask> subTasks;

    public Task() {
    }

    public Task(Long subProjectId, Long id, String name, String description, LocalDate deadline, Double estimatedHours) {
        this.subProjectId = subProjectId;
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.estimatedHours = estimatedHours;
    }

    public Long getSubProjectId() {
        return subProjectId;
    }

    public void setSubProjectId(Long subProjectId) {
        this.subProjectId = subProjectId;
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

    public Double getStoredEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    // if subtasks exist, sum their estimated hours; otherwise return task stored estimated hours
    public double getEstimatedHours() {
        if (subTasks == null || subTasks.isEmpty()) {
            if (estimatedHours == null || estimatedHours <= 0) {
                return 0.0;
            }
            return estimatedHours;
        }

        double total = 0.0;
        for (SubTask subTask : subTasks) {
            total += subTask.getEstimatedHours();
        }

        return total;
    }
}
