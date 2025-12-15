package com.example.projectcalculator.controller;

import com.example.projectcalculator.dto.ProjectDto;
import com.example.projectcalculator.model.Project;
import com.example.projectcalculator.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Spring MVC Controller for handling HTTP requests related to {@link Project} management.
 *
 * <p>This controller manages the presentation layer for project operations in the
 * Project Calculator web application. It handles both form-based interactions (Thymeleaf)
 * and provides a debug endpoint for API-style responses.</p>
 *
 * <p><b>Controller Mapping:</b></p>
 * <p>All endpoints in this controller are prefixed with {@code /projects} as defined
 * by the class-level {@link RequestMapping} annotation.</p>
 *
 * <p><b>Thymeleaf Views:</b></p>
 * <p>This controller returns view names that correspond to Thymeleaf templates in the
 * {@code src/main/resources/templates/} directory:</p>
 * <ul>
 *   <li><b>projects:</b> Main project listing page</li>
 *   <li><b>createProjectForm:</b> Form for creating new projects</li>
 * </ul>
 *
 * <p><b>Flow Control:</b></p>
 * <ol>
 *   <li>User navigates to {@code /projects} → shows project list</li>
 *   <li>User clicks "Create Project" → shows form</li>
 *   <li>User submits form → validates, creates project, redirects to list</li>
 *   <li>User clicks "Delete" → deletes project, redirects with status message</li>
 * </ol>
 *
 * <p><b>Validation:</b></p>
 * <p>The controller uses Spring's validation framework with {@link Valid} annotations
 * and {@link BindingResult} for error handling. Validation rules are defined in
 * {@link ProjectDto} using Jakarta Bean Validation annotations.</p>
 *
 * <p><b>Example HTTP Requests:</b></p>
 * <pre>
 * GET  /projects                     → Returns projects.html with project list
 * GET  /projects/create              → Returns createProjectForm.html with empty form
 * POST /projects                     → Creates new project, redirects to /projects
 * POST /projects/123/delete          → Deletes project with ID 123
 * GET  /projects/debug/list          → Returns JSON list of projects (debug only)
 * </pre>
 *
 * @author Arian & Magnus
 * @version 1.0
 * @since 1.0
 * @see Project
 * @see ProjectDto
 * @see ProjectService
 * @see Controller
 * @see RequestMapping
 */
@Controller
@RequestMapping("/projects")
public class ProjectController {

    /** Service layer for project business logic and operations. */
    private final ProjectService service;

    /**
     * Constructs a new ProjectController with the specified service.
     *
     * <p>This constructor is automatically wired by Spring's dependency injection.
     * The ProjectService is required for all controller operations.</p>
     *
     * @param service the ProjectService to use for business operations
     * @throws IllegalArgumentException if {@code service} is {@code null}
     */
    public ProjectController(ProjectService service) {
        this.service = service;
    }

    /**
     * Displays the main project listing page.
     *
     * <p>This handler method processes GET requests to {@code /projects} and prepares
     * the model for the Thymeleaf template. It retrieves all projects from the service
     * layer and adds them to the model.</p>
     *
     * <p><b>Model Attributes:</b></p>
     * <ul>
     *   <li><b>projects:</b> {@link List}&lt;{@link Project}&gt; - All projects in the system</li>
     * </ul>
     *
     * @param model the Spring MVC Model object for passing data to the view
     * @return the view name "projects" (resolves to projects.html)
     *
     * @see ProjectService#getAllProjects()
     * @see GetMapping
     */
    @GetMapping
    public String showProjects(Model model) {
        List<Project> projects = service.getAllProjects();
        model.addAttribute("projects", projects);
        return "projects";
    }

    /**
     * Displays the form for creating a new project.
     *
     * <p>This handler method processes GET requests to {@code /projects/create} and
     * prepares an empty {@link ProjectDto} for the form. The form will be bound to
     * this DTO for validation and data transfer.</p>
     *
     * <p><b>Model Attributes:</b></p>
     * <ul>
     *   <li><b>projectDto:</b> {@link ProjectDto} - Empty DTO for form binding</li>
     * </ul>
     *
     * @param model the Spring MVC Model object for passing data to the view
     * @return the view name "createProjectForm" (resolves to createProjectForm.html)
     *
     * @see GetMapping
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("projectDto", new ProjectDto());
        return "createProjectForm";
    }

    /**
     * Processes the submission of a new project creation form.
     *
     * <p>This handler method processes POST requests to {@code /projects} with form data.
     * It performs validation on the submitted {@link ProjectDto} and either:
     * <ul>
     *   <li>Returns to the form with validation errors if validation fails</li>
     *   <li>Creates the project and redirects to the project list if validation succeeds</li>
     * </ul></p>
     *
     * <p><b>Form Binding:</b></p>
     * <p>The {@link ModelAttribute} annotation binds form fields to the {@link ProjectDto}
     * object, and the {@link Valid} annotation triggers validation based on the
     * constraints defined in the DTO.</p>
     *
     * <p><b>Redirect Pattern:</b></p>
     * <p>After successful creation, the method uses the Post-Redirect-Get (PRG) pattern
     * to prevent duplicate form submissions.</p>
     *
     * @param projectDto the data transfer object populated from form submission
     * @param br the binding result containing validation errors (if any)
     * @return either "createProjectForm" (if validation errors) or "redirect:/projects"
     *
     * @throws org.springframework.web.bind.MethodArgumentNotValidException if validation fails
     *
     * @see PostMapping
     * @see Valid
     * @see BindingResult
     * @see ProjectService#create(ProjectDto)
     */
    @PostMapping
    public String createProject(
            @Valid @ModelAttribute("projectDto")
            ProjectDto projectDto,
            BindingResult br) {

        if (br.hasErrors()) return "createProjectForm";

        service.create(projectDto);
        return "redirect:/projects";
    }

    /**
     * Deletes a project by its ID.
     *
     * <p>This handler method processes POST requests to {@code /projects/{id}/delete}
     * and attempts to delete the specified project. The result is communicated to
     * the user via query parameters in the redirect URL.</p>
     *
     * <p><b>URL Pattern:</b> {@code /projects/{id}/delete}</p>
     *
     * <p><b>Success/Failure Handling:</b></p>
     * <ul>
     *   <li><b>Success:</b> Redirects with {@code ?success=Project deleted successfully}</li>
     *   <li><b>Failure:</b> Redirects with {@code ?error=Could not delete project}</li>
     * </ul>
     *
     * <p><b>Security Note:</b> This endpoint uses POST for deletion (not DELETE HTTP method)
     * to work with HTML forms. Consider CSRF protection if implemented.</p>
     *
     * @param id the ID of the project to delete (extracted from URL path)
     * @return redirect to {@code /projects} with success or error message parameter
     *
     * @see PostMapping
     * @see PathVariable
     * @see ProjectService#delete(long)
     */
    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable long id) {
        boolean deleted = service.delete(id);

        if (!deleted) {
            return "redirect:/projects?error=Could not delete project";
        }
        return "redirect:/projects?success=Project deleted successfully";
    }

    /**
     * Debug endpoint that returns all projects as JSON (or other representation).
     *
     * <p>This handler method processes GET requests to {@code /projects/debug/list}
     * and returns the list of projects directly as an HTTP response body. The
     * {@link ResponseBody} annotation bypasses view resolution and uses Spring's
     * HTTP message converters to serialize the response.</p>
     *
     * <p><b>Purpose:</b> This endpoint is useful for debugging, API testing, or
     * integration with other systems. It returns data in a format determined by
     * content negotiation (typically JSON if Jackson is in the classpath).</p>
     *
     * <p><b>Security Consideration:</b> This endpoint is publicly accessible and
     * returns all project data. In a production environment, consider:
     * <ul>
     *   <li>Removing or securing this endpoint</li>
     *   <li>Adding authentication/authorization</li>
     *   <li>Limiting data exposure (DTO projection)</li>
     * </ul></p>
     *
     * @return a list of all projects, serialized as JSON (or other format)
     *
     * @see GetMapping
     * @see ResponseBody
     * @see ProjectService#getAllProjects()
     */
    @GetMapping("/debug/list")
    @ResponseBody
    public List<Project> debugList() {
        return service.getAllProjects();
    }
}