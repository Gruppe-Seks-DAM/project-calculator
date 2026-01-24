package com.example.projectcalculator.model;

import java.time.LocalDate;
import java.util.List;

public class Project {

    private Long id;
    private String name;
    private String description;
    private LocalDate deadline;

    private List<SubProject> subProjects;

    public Project() {
    }

    public Project(Long id, String name, String description, LocalDate deadline) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
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

    public List<SubProject> getSubProjects() {
        return subProjects;
    }

    public void setSubProjects(List<SubProject> subProjects) {
        this.subProjects = subProjects;
    }

    public double getEstimatedHours() {
        if (subProjects == null || subProjects.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (SubProject subProject : subProjects) {
            total += subProject.getEstimatedHours();
        }

        return total;
    }
}
