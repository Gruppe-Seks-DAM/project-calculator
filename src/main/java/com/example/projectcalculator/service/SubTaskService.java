package com.example.projectcalculator.service;

import com.example.projectcalculator.model.SubTask;
import com.example.projectcalculator.model.Task;
import com.example.projectcalculator.repository.SubTaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubTaskService {

    private final SubTaskRepository subTaskRepository;

    public SubTaskService(SubTaskRepository subTaskRepository) {
        this.subTaskRepository = subTaskRepository;
    }

    public List<SubTask> getAllSubTasks(long taskId) {
        return subTaskRepository.listAllSubTasksByTaskID(taskId);
    }

    public SubTask getSubTaskById(long taskId, long id) {
        return subTaskRepository.findSubTaskById(taskId, id);
    }

    public boolean createSubTask(SubTask subtask) {
        return subTaskRepository.createSubTask(subtask);
    }

    public boolean updateSubTask(SubTask subtask) {
        return subTaskRepository.updateSubTask(subtask);
    }

    public boolean deleteSubTask(long taskId, long id) {
        return subTaskRepository.deleteSubTask(taskId, id);
    }
}
