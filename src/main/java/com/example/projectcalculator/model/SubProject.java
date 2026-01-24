package com.example.projectcalculator.model;

import java.time.LocalDate;
import java.util.List;

public class SubProject {

    private Long projectId;
    private Long id;
    private String name;
    private String description;
    private LocalDate deadline;

    private List<Task> tasks;

    public SubProject() {
    }

    public SubProject(Long projectId, Long id, String name, String description, LocalDate deadline) {
        this.projectId = projectId;
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public double getEstimatedHours() {
        if (tasks == null || tasks.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (Task task : tasks) {
            total += task.getEstimatedHours();
        }

        return total;
    }
}
