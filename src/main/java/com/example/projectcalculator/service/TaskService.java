package com.example.projectcalculator.service;

import com.example.projectcalculator.model.Task;
import com.example.projectcalculator.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks(long subProjectId) {
        return taskRepository.listAllTasksBySubProjectId(subProjectId);
    }

    public Task getTaskById(long subProjectId, long id) {
        return taskRepository.findTaskById(subProjectId, id);
    }

    public boolean createTask(Task task) {
        return taskRepository.createTask(task);
    }

    public boolean updateTask(Task task) {
        return taskRepository.updateTask(task);
    }

    public boolean deleteTask(long subProjectId, long id) {
        return taskRepository.deleteTask(subProjectId, id);
    }
}
