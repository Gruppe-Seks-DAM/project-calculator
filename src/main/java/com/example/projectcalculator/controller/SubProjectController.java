package com.example.projectcalculator.controller;

import com.example.projectcalculator.dto.SubProjectDto;
import com.example.projectcalculator.service.SubProjectService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Spring MVC Controller for handling HTTP requests related to {@link com.example.projectcalculator.model.SubProject SubProject} management
 * within a specific parent {@link com.example.projectcalculator.model.Project Project}.
 *
 * <p>This controller manages the presentation layer for sub-project operations, which are always
 * scoped within a parent project. All endpoints require a {@code projectId} path variable to
 * establish the parent-child relationship.</p>
 *
 * <p><b>Controller Mapping:</b></p>
 * <p>All endpoints in this controller are prefixed with {@code /projects/{projectId}/subprojects}
 * as defined by the class-level {@link RequestMapping} annotation. The {@code projectId} path variable
 * must be present in every request and is used to scope all operations.</p>
 *
 * <p><b>Thymeleaf Views:</b></p>
 * <p>This controller returns view names that correspond to Thymeleaf templates in the
 * {@code src/main/resources/templates/} directory:</p>
 * <ul>
 *   <li><b>subprojects:</b> Sub-project listing page for a specific project</li>
 *   <li><b>createSubProjectForm:</b> Form for creating new sub-projects within a project</li>
 * </ul>
 *
 * <p><b>URL Patterns:</b></p>
 * <table border="1" style="border-collapse: collapse; width: 100%;">
 *   <tr><th>HTTP Method</th><th>URL Pattern</th><th>Purpose</th></tr>
 *   <tr><td>GET</td><td>/projects/{projectId}/subprojects</td><td>List all sub-projects for a project</td></tr>
 *   <tr><td>GET</td><td>/projects/{projectId}/subprojects/create</td><td>Show sub-project creation form</td></tr>
 *   <tr><td>POST</td><td>/projects/{projectId}/subprojects</td><td>Create a new sub-project</td></tr>
 *   <tr><td>POST</td><td>/projects/{projectId}/subprojects/{id}/delete</td><td>Delete a specific sub-project</td></tr>
 * </table>
 *
 * <p><b>Parent-Child Relationship:</b></p>
 * <p>Unlike {@link ProjectController}, which manages standalone projects, this controller always
 * operates within the context of a parent project. This design enforces the hierarchical relationship
 * between projects and sub-projects.</p>
 *
 * <p><b>Example HTTP Requests:</b></p>
 * <pre>
 * GET  /projects/123/subprojects                     → Returns subprojects.html for project 123
 * GET  /projects/123/subprojects/create              → Returns createSubProjectForm.html for project 123
 * POST /projects/123/subprojects                     → Creates sub-project within project 123
 * POST /projects/123/subprojects/456/delete          → Deletes sub-project 456 from project 123
 * </pre>
 *
 * <p><b>Security Consideration:</b> The current implementation assumes the projectId in the URL
 * is valid and the user has permission to access it. Consider adding authorization checks in
 * a production environment.</p>
 *
 * @author Magnus
 * @version 1.0
 * @since 1.0
 * @see com.example.projectcalculator.model.SubProject
 * @see com.example.projectcalculator.model.Project
 * @see SubProjectDto
 * @see SubProjectService
 * @see Controller
 * @see RequestMapping
 * @see ProjectController
 */
@Controller
@RequestMapping("/projects/{projectId}/subprojects")
public class SubProjectController {

    /** Service layer for sub-project business logic and operations. */
    private final SubProjectService service;

    /**
     * Constructs a new SubProjectController with the specified service.
     *
     * <p>This constructor is automatically wired by Spring's dependency injection.
     * The SubProjectService is required for all controller operations.</p>
     *
     * @param service the SubProjectService to use for business operations
     * @throws IllegalArgumentException if {@code service} is {@code null}
     */
    public SubProjectController(SubProjectService service) {
        this.service = service;
    }

    /**
     * Displays the sub-project listing page for a specific parent project.
     *
     * <p>This handler method processes GET requests to {@code /projects/{projectId}/subprojects}
     * and prepares the model for the Thymeleaf template. It retrieves all sub-projects
     * (across all projects) and adds them to the model along with the current project ID.</p>
     *
     * <p><b>Note:</b> Currently retrieves ALL sub-projects, not filtered by projectId.
     * This might be confusing for users. Consider modifying the service to filter by projectId.</p>
     *
     * <p><b>Model Attributes:</b></p>
     * <ul>
     *   <li><b>subprojects:</b> {@link java.util.List}&lt;{@link com.example.projectcalculator.model.SubProject}&gt; -
     *       All sub-projects in the system (currently unfiltered)</li>
     *   <li><b>projectId:</b> {@link Long} - The current parent project ID from the URL path</li>
     * </ul>
     *
     * @param projectId the ID of the parent project (extracted from URL path)
     * @param model the Spring MVC Model object for passing data to the view
     * @return the view name "subprojects" (resolves to subprojects.html)
     *
     * @see GetMapping
     * @see PathVariable
     * @see SubProjectService#getAllSubProjects()
     */
    @GetMapping
    public String listSubProjects(@PathVariable long projectId, Model model) {
        model.addAttribute("subprojects", service.getAllSubProjects());
        model.addAttribute("projectId", projectId);
        return "subprojects";
    }

    /**
     * Displays the form for creating a new sub-project within a specific parent project.
     *
     * <p>This handler method processes GET requests to {@code /projects/{projectId}/subprojects/create}
     * and prepares an empty {@link SubProjectDto} for the form. The form will be bound to
     * this DTO for validation and data transfer.</p>
     *
     * <p><b>Model Attributes:</b></p>
     * <ul>
     *   <li><b>subProjectDto:</b> {@link SubProjectDto} - Empty DTO for form binding</li>
     *   <li><b>projectId:</b> {@link Long} - The current parent project ID from the URL path</li>
     * </ul>
     *
     * @param projectId the ID of the parent project (extracted from URL path)
     * @param model the Spring MVC Model object for passing data to the view
     * @return the view name "createSubProjectForm" (resolves to createSubProjectForm.html)
     *
     * @see GetMapping
     * @see PathVariable
     */
    @GetMapping("/create")
    public String showCreateForm(@PathVariable long projectId, Model model) {
        model.addAttribute("subProjectDto", new SubProjectDto());
        model.addAttribute("projectId", projectId);
        return "createSubProjectForm";
    }

    /**
     * Processes the submission of a new sub-project creation form.
     *
     * <p>This handler method processes POST requests to {@code /projects/{projectId}/subprojects}
     * with form data. It performs validation on the submitted {@link SubProjectDto} and either:
     * <ul>
     *   <li>Returns to the form with validation errors if validation fails</li>
     *   <li>Creates the sub-project and redirects to the sub-project list if validation succeeds</li>
     * </ul></p>
     *
     * <p><b>Form Binding:</b></p>
     * <p>The {@link ModelAttribute} annotation binds form fields to the {@link SubProjectDto}
     * object, and the {@link Valid} annotation triggers validation based on the
     * constraints defined in the DTO.</p>
     *
     * <p><b>Success/Failure Handling:</b></p>
     * <ul>
     *   <li><b>Success:</b> Redirects with {@code ?success=Subproject created}</li>
     *   <li><b>Failure:</b> Redirects with {@code ?error=Could not create subproject}</li>
     * </ul>
     *
     * <p><b>Note:</b> The service layer may throw an exception if the parent project doesn't exist.
     * Currently, this would result in a 500 error. Consider adding proper exception handling.</p>
     *
     * @param projectId the ID of the parent project (extracted from URL path)
     * @param subProjectDto the data transfer object populated from form submission
     * @param br the binding result containing validation errors (if any)
     * @return either "createSubProjectForm" (if validation errors) or redirect to sub-project list
     *
     * @throws IllegalArgumentException if service layer validation fails (e.g., parent project doesn't exist)
     *
     * @see PostMapping
     * @see Valid
     * @see BindingResult
     * @see SubProjectService#create(SubProjectDto, long)
     */
    @PostMapping
    public String createSubProject(@PathVariable long projectId,
                                   @Valid @ModelAttribute("subProjectDto") SubProjectDto subProjectDto,
                                   BindingResult br) {
        if (br.hasErrors()) {
            return "createSubProjectForm";
        }

        boolean created = service.create(subProjectDto, projectId);
        if (!created) {
            return "redirect:/projects/" + projectId + "/subprojects?error=Could not create subproject";
        }

        return "redirect:/projects/" + projectId + "/subprojects?success=Subproject created";
    }

    /**
     * Deletes a sub-project by its ID within a specific parent project.
     *
     * <p>This handler method processes POST requests to {@code /projects/{projectId}/subprojects/{id}/delete}
     * and attempts to delete the specified sub-project. The result is communicated to
     * the user via query parameters in the redirect URL.</p>
     *
     * <p><b>URL Pattern:</b> {@code /projects/{projectId}/subprojects/{id}/delete}</p>
     *
     * <p><b>Success/Failure Handling:</b></p>
     * <ul>
     *   <li><b>Success:</b> Redirects with {@code ?success=Subproject deleted}</li>
     *   <li><b>Failure:</b> Redirects with {@code ?error=Could not delete subproject}</li>
     * </ul>
     *
     * <p><b>Important Considerations:</b></p>
     * <ul>
     *   <li>The deletion may fail if the sub-project has associated tasks (foreign key constraints)</li>
     *   <li>The method doesn't verify that the sub-project belongs to the specified parent project</li>
     *   <li>No cascade deletion of tasks is performed at the controller level</li>
     * </ul>
     *
     * <p><b>Security Note:</b> This endpoint doesn't validate that the sub-project actually belongs
     * to the parent project specified in the URL. Consider adding authorization logic.</p>
     *
     * @param projectId the ID of the parent project (extracted from URL path)
     * @param id the ID of the sub-project to delete (extracted from URL path)
     * @return redirect to {@code /projects/{projectId}/subprojects} with success or error message parameter
     *
     * @see PostMapping
     * @see PathVariable
     * @see SubProjectService#delete(long)
     */
    @PostMapping("/{id}/delete")
    public String deleteSubProject(@PathVariable long projectId, @PathVariable long id) {
        boolean deleted = service.delete(id);
        if (!deleted) {
            return "redirect:/projects/" + projectId + "/subprojects?error=Could not delete subproject";
        }
        return "redirect:/projects/" + projectId + "/subprojects?success=Subproject deleted";
    }
}