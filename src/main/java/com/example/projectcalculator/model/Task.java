package com.example.projectcalculator.model;

import java.time.LocalDate;

public class Task {

    private Long subProjectId;
    private Long id;
    private String name;
    private String description;
    private LocalDate deadline;
    private Double estimatedHours;


    public Task() {}


    public Task(Long subProjectId,Long id, String name, String description, LocalDate deadline, Double estimatedHours) {
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

    public Double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }
}