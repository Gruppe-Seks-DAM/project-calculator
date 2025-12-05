package com.example.projectcalculator.service;

import com.example.projectcalculator.model.Task;
import com.example.projectcalculator.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Optional<Task> findById(Long id) {
        return repository.findById(id);
    }

    public boolean updateTask(Task task) {
        return repository.update(task);
    }
}