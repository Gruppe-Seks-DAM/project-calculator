package com.example.projectcalculator.controller;

import com.example.projectcalculator.dto.SubTaskDto;
import com.example.projectcalculator.model.SubTask;
import com.example.projectcalculator.service.SubTaskService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
public class SubTaskController {

    private final SubTaskService service;

    public SubTaskController(SubTaskService service) {
        this.service = service;
    }

    // GET /tasks/{taskId}/subtasks/create
    @GetMapping("/{taskId}/subtasks/create")
    public String showCreateForm(@PathVariable Long taskId, Model model) {
        model.addAttribute("subtaskDto", new SubTaskDto());
        model.addAttribute("taskId", taskId);
        return "subtasks/create";
    }

    // POST /tasks/{taskId}/subtasks
    @PostMapping("/{taskId}/subtasks")
    public String createSubtask(
            @PathVariable Long taskId,
            @Valid @ModelAttribute("subtaskDto") SubTaskDto dto,
            BindingResult br,
            Model model
    ) {
        if (br.hasErrors()) {
            model.addAttribute("taskId", taskId);
            return "subtasks/create";
        }

        SubTask subtask = new SubTask();
        subtask.setTaskId(taskId);
        subtask.setName(dto.getName());
        subtask.setDescription(dto.getDescription());
        subtask.setDeadline(dto.getDeadline());
        subtask.setEstimatedHours(dto.getEstimatedHours());

        boolean created = service.createSubtask(subtask);
        if (!created) {
            model.addAttribute("taskId", taskId);
            model.addAttribute("error", "Failed to create subtask");
            return "subtasks/create";
        }

        return "redirect:/tasks?success=Subtask created";
    }
}