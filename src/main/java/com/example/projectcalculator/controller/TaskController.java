package com.example.projectcalculator.controller;

import com.example.projectcalculator.dto.TaskDto;
import com.example.projectcalculator.model.Task;
import com.example.projectcalculator.service.TaskService;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Spring MVC Controller for handling HTTP requests related to {@link Task} management.
 *
 * <p>This controller manages the presentation layer for task operations in the
 * Project Calculator web application. It provides a mix of hierarchical and standalone
 * endpoints for managing tasks, which can exist within sub-projects or be managed independently.</p>
 *
 * <p><b>Controller Structure:</b></p>
 * <p>This controller uses two URL patterns:</p>
 * <ul>
 *   <li><b>Standalone operations:</b> {@code /tasks/{id}/*} for operations on specific tasks</li>
 *   <li><b>Hierarchical operations:</b> {@code /tasks/{subProjectId}/tasks/*} for operations within sub-projects</li>
 * </ul>
 *
 * <p><b>Thymeleaf Views:</b></p>
 * <p>This controller returns view names that correspond to Thymeleaf templates in the
 * {@code src/main/resources/templates/} directory:</p>
 * <ul>
 *   <li><b>tasks/list:</b> Task listing for a specific sub-project</li>
 *   <li><b>tasks/create:</b> Form for creating new tasks</li>
 *   <li><b>tasks/edit:</b> Form for editing existing tasks</li>
 *   <li><b>tasks/confirm-delete:</b> Confirmation page for task deletion</li>
 * </ul>
 *
 * <p><b>URL Pattern Mix:</b></p>
 * <p>This controller handles endpoints in two patterns which might cause confusion:</p>
 * <ol>
 *   <li>{@code /tasks/{id}/delete} - Deletes a specific task by ID</li>
 *   <li>{@code /tasks/{subProjectId}/tasks/create} - Creates a task within a sub-project</li>
 * </ol>
 * <p><b>Note:</b> This mixing of patterns could lead to ambiguous URL mappings. Consider
 * restructuring to use consistent hierarchical patterns.</p>
 *
 * <p><b>Example HTTP Requests:</b></p>
 * <pre>
 * // Standalone task operations
 * GET  /tasks/123/delete                    → Shows delete confirmation for task 123
 * POST /tasks/123/delete                    → Deletes task 123, redirects to parent
 * GET  /tasks/123/edit                      → Shows edit form for task 123
 * POST /tasks/123                           → Updates task 123
 *
 * // Hierarchical task operations within sub-projects
 * GET  /tasks/456                           → Lists all tasks for sub-project 456
 * GET  /tasks/456/tasks/create              → Shows create form for task in sub-project 456
 * POST /tasks/456/tasks                     → Creates task in sub-project 456
 * </pre>
 *
 * <p><b>Error Handling Strategy:</b></p>
 * <p>This controller uses a combination of approaches:</p>
 * <ul>
 *   <li>Validation errors: Return to form with error messages</li>
 *   <li>Service errors: Redirect with error messages or return to form</li>
 *   <li>Missing resources: Redirect to appropriate parent page</li>
 * </ul>
 *
 * @author Daniel
 * @version 1.0
 * @since 1.0
 * @see Task
 * @see TaskDto
 * @see TaskService
 * @see Controller
 * @see RequestMapping
 * @see SubProjectController
 */
@Controller
@RequestMapping("/tasks")
public class TaskController {

    /** Service layer for task business logic and operations. */
    private final TaskService service;

    /**
     * Constructs a new TaskController with the specified service.
     *
     * <p>This constructor uses package-private visibility, which means it can only
     * be called within the same package or by Spring's dependency injection framework.
     * The TaskService is required for all controller operations.</p>
     *
     * <p><b>Note:</b> The package-private constructor is unusual for Spring controllers.
     * Typically, controllers use public constructors. Consider making it public if
     * other packages need to instantiate the controller.</p>
     *
     * @param service the TaskService to use for business operations
     * @throws IllegalArgumentException if {@code service} is {@code null}
     */
    TaskController(TaskService service){
        this.service = service;
    }

    /**
     * Processes the deletion of a task by its ID.
     *
     * <p>This handler method processes POST requests to {@code /tasks/{id}/delete}
     * and performs the following steps:</p>
     * <ol>
     *   <li>Retrieves the parent sub-project ID for navigation after deletion</li>
     *   <li>Calls the service to delete the task</li>
     *   <li>Redirects to the parent sub-project if deletion successful</li>
     *   <li>Handles various error scenarios with appropriate redirects</li>
     * </ol>
     *
     * <p><b>Redirect Logic:</b></p>
     * <ul>
     *   <li>Success with known parent: Redirects to sub-project page</li>
     *   <li>Success without parent: Redirects to projects page</li>
     *   <li>Task not found: Redirects to projects with error message</li>
     *   <li>Exception: Redirects to projects with error message</li>
     * </ul>
     *
     * <p><b>Model Usage:</b> Uses the Model to store error messages, but since the method
     * always redirects, these messages are lost. Consider using flash attributes instead.</p>
     *
     * @param id the ID of the task to delete (extracted from URL path)
     * @param model the Spring MVC Model object (note: messages lost on redirect)
     * @return redirect to parent sub-project or projects page
     *
     * @see PostMapping
     * @see PathVariable
     * @see TaskService#getSubProjectIdForTask(Long)
     * @see TaskService#deleteTask(Long)
     */
    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id, Model model) {
        try {

            Optional<Long> subProjectIdOpt = service.getSubProjectIdForTask(id);


            boolean deleted = service.deleteTask(id);

            if (deleted) {

                if (subProjectIdOpt.isPresent()) {
                    return "redirect:/subprojects/" + subProjectIdOpt.get();
                } else {
                    return "redirect:/projects";
                }
            } else {
                model.addAttribute("error", "Task not found or could not be deleted");

                return "redirect:/projects";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting task: " + e.getMessage());
            return "redirect:/projects";
        }
    }

    /**
     * Displays the confirmation page for task deletion.
     *
     * <p>This handler method processes GET requests to {@code /tasks/{id}/delete}
     * and shows a confirmation page before actual deletion. This follows the
     * principle of requiring user confirmation for destructive operations.</p>
     *
     * <p><b>Model Attributes:</b></p>
     * <ul>
     *   <li><b>task:</b> {@link Task} - The task to be deleted (if found)</li>
     * </ul>
     *
     * <p><b>Flow:</b></p>
     * <ol>
     *   <li>User requests deletion (e.g., via link)</li>
     *   <li>This method shows confirmation page</li>
     *   <li>User confirms → POST to {@code /tasks/{id}/delete}</li>
     *   <li>User cancels → Navigates away</li>
     * </ol>
     *
     * @param id the ID of the task to confirm deletion for
     * @param model the Spring MVC Model object for passing data to the view
     * @return either "tasks/confirm-delete" or redirect to projects if task not found
     *
     * @see GetMapping
     * @see PathVariable
     * @see TaskService#getTaskById(Long)
     */
    @GetMapping("/{id}/delete")
    public String confirmDelete(@PathVariable Long id, Model model) {
        Optional<Task> taskOpt = service.getTaskById(id);

        if (taskOpt.isEmpty()) {
            return "redirect:/projects";
        }

        model.addAttribute("task", taskOpt.get());
        return "tasks/confirm-delete";
    }

    /**
     * Displays the form for editing an existing task.
     *
     * <p>This handler method processes GET requests to {@code /tasks/{id}/edit}
     * and prepares the edit form with current task data. It converts a {@link Task}
     * entity to a {@link TaskDto} for form binding.</p>
     *
     * <p><b>Model Attributes:</b></p>
     * <ul>
     *   <li><b>taskDto:</b> {@link TaskDto} - DTO populated with current task data</li>
     *   <li><b>taskId:</b> {@link Long} - The ID of the task being edited</li>
     * </ul>
     *
     * <p><b>Note:</b> The edit form only handles name, description, and deadline.
     * Estimated hours and sub-project ID are not editable in this form, which may
     * be intentional or an oversight.</p>
     *
     * @param id the ID of the task to edit
     * @param model the Spring MVC Model object for passing data to the view
     * @return either "tasks/edit" or redirect to tasks list if task not found
     *
     * @see GetMapping
     * @see PathVariable
     * @see TaskService#findById(Long)
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Task> opt = service.findById(id);
        if (opt.isEmpty()) return "redirect:/tasks?error=Task not found";

        Task t = opt.get();
        TaskDto dto = new TaskDto();
        dto.setName(t.getName());
        dto.setDescription(t.getDescription());
        dto.setDeadline(t.getDeadline());

        model.addAttribute("taskDto", dto);
        model.addAttribute("taskId", id);
        return "tasks/edit";
    }

    /**
     * Processes the submission of a task edit form.
     *
     * <p>This handler method processes POST requests to {@code /tasks/{id}}
     * with updated task data. It performs validation and updates the task
     * if validation passes.</p>
     *
     * <p><b>Important Limitations:</b></p>
     * <ul>
     *   <li>Only updates name, description, and deadline (not estimated hours or sub-project)</li>
     *   <li>Creates a new Task object instead of updating the existing one directly</li>
     *   <li>Does not preserve the sub-project ID from the original task</li>
     *   <li>Returns to edit form on error but redirects to tasks list on success (inconsistent)</li>
     * </ul>
     *
     * <p><b>Validation:</b></p>
     * <p>Uses {@link Valid} annotation to trigger validation defined in {@link TaskDto}.
     * However, note that the DTO validation is incomplete since it doesn't include
     * estimated hours validation in this context.</p>
     *
     * @param id the ID of the task to update
     * @param dto the data transfer object with updated values
     * @param br the binding result containing validation errors
     * @param model the Spring MVC Model object for passing data to the view
     * @return either "tasks/edit" (if errors) or redirect to tasks list
     *
     * @see PostMapping
     * @see Valid
     * @see BindingResult
     * @see TaskService#updateTask(Task)
     */
    @PostMapping("/{id}")
    public String updateTask(
            @PathVariable Long id,
            @Valid @ModelAttribute("taskDto") TaskDto dto,
            BindingResult br,
            Model model
    ) {
        if (br.hasErrors()) {
            model.addAttribute("taskId", id);
            return "tasks/edit";
        }

        Task t = new Task();
        t.setId(id);
        t.setName(dto.getName());
        t.setDescription(dto.getDescription());
        t.setDeadline(dto.getDeadline());

        boolean updated = service.updateTask(t);
        if (!updated) {
            model.addAttribute("error", "Failed to update task");
            model.addAttribute("taskId", id);
            return "tasks/edit";
        }
        return "redirect:/tasks?success=Task updated";
    }

    /**
     * Displays the form for creating a new task within a specific sub-project.
     *
     * <p>This handler method processes GET requests to {@code /tasks/{subProjectId}/tasks/create}
     * and prepares a new {@link Task} object with the sub-project ID pre-set.
     * This ensures the task is created within the correct sub-project context.</p>
     *
     * <p><b>Model Attributes:</b></p>
     * <ul>
     *   <li><b>task:</b> {@link Task} - New task object with sub-project ID set</li>
     * </ul>
     *
     * <p><b>URL Pattern Issue:</b> This endpoint uses a confusing URL pattern
     * {@code /tasks/{subProjectId}/tasks/create} which includes "tasks" twice.
     * Consider restructuring to {@code /subprojects/{subProjectId}/tasks/create}
     * for clearer hierarchy.</p>
     *
     * @param subProjectId the ID of the parent sub-project
     * @param model the Spring MVC Model object for passing data to the view
     * @return the view name "tasks/create" (resolves to tasks/create.html)
     *
     * @see GetMapping
     * @see PathVariable
     */
    @GetMapping("/{subProjectId}/tasks/create")
    public String showCreateForm(@PathVariable Long subProjectId, Model model) {
        Task task = new Task();
        task.setSubProjectId(subProjectId);
        model.addAttribute("task", task);
        return "tasks/create";
    }

    /**
     * Processes the submission of a new task creation form within a sub-project.
     *
     * <p>This handler method processes POST requests to {@code /tasks/{subProjectId}/tasks}
     * and creates a new task within the specified sub-project. It includes
     * comprehensive error handling and validation.</p>
     *
     * <p><b>Key Features:</b></p>
     * <ul>
     *   <li>Sets the sub-project ID on the task (ensures correct parent association)</li>
     *   <li>Validates the task using Jakarta Bean Validation</li>
     *   <li>Handles service-layer exceptions (e.g., validation failures)</li>
     *   <li>Redirects to parent sub-project on success</li>
     * </ul>
     *
     * <p><b>Important:</b> This method binds directly to a {@link Task} entity
     * rather than a {@link TaskDto}. This is unusual in Spring MVC patterns where
     * DTOs are typically used for form binding to separate presentation from domain.</p>
     *
     * @param subProjectId the ID of the parent sub-project
     * @param task the task entity populated from form submission
     * @param bindingResult the binding result containing validation errors
     * @param model the Spring MVC Model object for passing data to the view
     * @return either "tasks/create" (if errors) or redirect to parent sub-project
     *
     * @throws IllegalArgumentException if service layer validation fails
     *
     * @see PostMapping
     * @see Valid
     * @see BindingResult
     * @see TaskService#createTask(Task)
     */
    @PostMapping("/{subProjectId}/tasks")
    public String createTask(
            @PathVariable Long subProjectId,
            @Valid @ModelAttribute("task") Task task,
            BindingResult bindingResult,
            Model model) {


        task.setSubProjectId(subProjectId);

        if (bindingResult.hasErrors()) {
            return "tasks/create";
        }

        try {
            service.createTask(task);
            return "redirect:/subprojects/" + subProjectId;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "tasks/create";
        }
    }

    /**
     * Lists all tasks for a specific sub-project.
     *
     * <p>This handler method processes GET requests to {@code /tasks/{subProjectId}}
     * and retrieves all tasks associated with the specified sub-project. Tasks
     * are ordered by deadline in the service layer.</p>
     *
     * <p><b>Model Attributes:</b></p>
     * <ul>
     *   <li><b>tasks:</b> {@link List}&lt;{@link Task}&gt; - Tasks for the sub-project</li>
     *   <li><b>subProjectId:</b> {@link Long} - The ID of the parent sub-project</li>
     * </ul>
     *
     * <p><b>URL Pattern Issue:</b> This endpoint uses {@code /tasks/{subProjectId}}
     * which conflicts with the standalone task operations pattern
     * {@code /tasks/{id}}. This ambiguity could cause routing issues.</p>
     *
     * @param subProjectId the ID of the sub-project to list tasks for
     * @param model the Spring MVC Model object for passing data to the view
     * @return the view name "tasks/list" (resolves to tasks/list.html)
     *
     * @see GetMapping
     * @see PathVariable
     * @see TaskService#getTasksBySubProjectId(Long)
     */
    @GetMapping("/{subProjectId}")
    public String viewSubProjectTasks(@PathVariable Long subProjectId, Model model) {
        List<Task> tasks = service.getTasksBySubProjectId(subProjectId);
        model.addAttribute("tasks", tasks);
        model.addAttribute("subProjectId", subProjectId);
        return "tasks/list";
    }
}