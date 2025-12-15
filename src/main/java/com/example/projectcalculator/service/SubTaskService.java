package com.example.projectcalculator.service;

import com.example.projectcalculator.model.SubTask;
import com.example.projectcalculator.repository.SubTaskRepository;
import org.springframework.stereotype.Service;

@Service
public class SubTaskService {

    private final SubTaskRepository repository;

    public SubTaskService(SubTaskRepository repository) {
        this.repository = repository;
    }

    public boolean createSubtask(SubTask subtask) {
        return repository.create(subtask);
    }
}