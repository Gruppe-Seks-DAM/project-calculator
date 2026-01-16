package com.example.projectcalculator.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public class SubProject {

    private Long id;
    
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name cannot contain more than 50 characters")
    private String name;
    
    @Size(max = 200, message = "Description cannot contain more than 200 characters")
    private String description;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deadline;

    public SubProject() {
    }

    public SubProject(Long id, String name, String description, LocalDate deadline, List<Task> tasks) {
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
}
