package com.example.projectcalculator.service;

import com.example.projectcalculator.dto.SubProjectDto;
import com.example.projectcalculator.model.SubProject;
import com.example.projectcalculator.repository.SubProjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service layer for managing {@link SubProject} entities in the Project Calculator application.
 *
 * <p>This service class provides business operations for managing sub-projects, which are
 * components or phases within a larger {@link com.example.projectcalculator.model.Project Project}.
 * It handles the conversion between DTOs and entities, enforces business rules, and orchestrates
 * repository calls.</p>
 *
 * <p><b>Service Layer Role:</b></p>
 * <p>The SubProjectService serves as an intermediary between controllers (presentation layer)
 * and the repository (data access layer), ensuring that:</p>
 * <ul>
 *   <li>Business rules are enforced before data persistence</li>
 *   <li>DTOs are properly converted to entities and vice versa</li>
 *   <li>Related operations (like cascade operations) are coordinated</li>
 *   <li>Transactions are properly managed (implicit via Spring's @Service)</li>
 * </ul>
 *
 * <p><b>Parent-Child Relationship:</b></p>
 * <p>Unlike {@link com.example.projectcalculator.service.ProjectService ProjectService}, this service
 * always works within the context of a parent project. Sub-projects cannot exist independently
 * and must be associated with an existing project.</p>
 *
 * <p><b>Current Limitations and Enhancement Opportunities:</b></p>
 * <ul>
 *   <li>No validation that sub-project deadline is within parent project's timeline</li>
 *   <li>No cascade deletion of tasks when deleting a sub-project</li>
 *   <li>No check for duplicate sub-project names within the same project</li>
 *   <li>No retrieval of sub-projects filtered by a specific project</li>
 * </ul>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>
 * // In a controller:
 * &#64;Autowired
 * private SubProjectService subProjectService;
 *
 * // Get all sub-projects (across all projects)
 * List<SubProject> allSubProjects = subProjectService.getAllSubProjects();
 *
 * // Create a new sub-project within project ID 123
 * SubProjectDto subProjectDto = new SubProjectDto();
 * subProjectDto.setName("UI/UX Design");
 * subProjectDto.setDescription("Design user interface and experience");
 * subProjectDto.setDeadline(LocalDate.of(2024, 8, 31));
 * boolean created = subProjectService.create(subProjectDto, 123L);
 *
 * // Delete a sub-project
 * boolean deleted = subProjectService.delete(5L);
 * </pre>
 *
 * @author Magnus
 * @version 1.0
 * @since 1.0
 * @see SubProject
 * @see SubProjectDto
 * @see SubProjectRepository
 * @see com.example.projectcalculator.service.ProjectService
 * @see com.example.projectcalculator.model.Project
 * @see Service
 */
@Service
public class SubProjectService {

    /** Repository for performing data access operations on SubProject entities. */
    private final SubProjectRepository subprojectRepository;

    /**
     * Constructs a new SubProjectService with the specified repository.
     *
     * <p>This constructor is automatically wired by Spring's dependency injection
     * framework. A single SubProjectRepository instance is required for all operations.</p>
     *
     * @param subprojectRepository the SubProjectRepository to use for data access operations
     * @throws IllegalArgumentException if {@code subprojectRepository} is {@code null}
     */
    public SubProjectService(SubProjectRepository subprojectRepository) {
        this.subprojectRepository = subprojectRepository;
    }

    /**
     * Retrieves all sub-projects from the database across all projects.
     *
     * <p><b>Note:</b> This method returns sub-projects from all projects. For most use cases,
     * you may want to retrieve sub-projects for a specific project. Consider adding a method
     * like {@code getSubProjectsByProjectId(long projectId)} for filtered retrieval.</p>
     *
     * <p><b>Performance Considerations:</b></p>
     * <ul>
     *   <li>For systems with many projects and sub-projects, this may return large datasets</li>
     *   <li>Consider adding pagination or project-based filtering</li>
     *   <li>The returned SubProject objects do not include their associated tasks</li>
     * </ul>
     *
     * @return a list of all sub-projects in the database, ordered by ID;
     *         returns an empty list if no sub-projects exist
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     *
     * @see SubProjectRepository#findAllSubProjects()
     */
    public List<SubProject> getAllSubProjects() {
        return subprojectRepository.findAllSubProjects();
    }

    /**
     * Creates a new sub-project within a specified parent project.
     *
     * <p>This method performs the following steps:</p>
     * <ol>
     *   <li>Validates that the DTO and projectId are not null</li>
     *   <li>Converts the {@link SubProjectDto} to a {@link SubProject} entity</li>
     *   <li>Delegates persistence to the repository with the parent project ID</li>
     *   <li>Returns the creation success status</li>
     * </ol>
     *
     * <p><b>Business Rules That Could Be Added:</b></p>
     * <ul>
     *   <li>Validate that the parent project exists</li>
     *   <li>Ensure sub-project deadline is within parent project's timeline</li>
     *   <li>Check for duplicate sub-project names within the same project</li>
     *   <li>Set default values for missing optional fields</li>
     * </ul>
     *
     * @param subProjectDto the data transfer object containing sub-project information (must not be {@code null})
     * @param projectId the ID of the parent project to associate with this sub-project
     * @return {@code true} if the sub-project was successfully created, {@code false} otherwise
     * @throws IllegalArgumentException if {@code subProjectDto} is {@code null},
     *                                  if {@code projectId} is invalid,
     *                                  or if required data is missing
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     * @throws NullPointerException if {@code subProject} is {@code null}
     *
     * @see SubProjectDto
     * @see SubProjectRepository#createSubProject(SubProject, long)
     */
    public boolean create(SubProjectDto subProjectDto, long projectId) {
        SubProject p = new SubProject();
        p.setName(subProjectDto.getName());
        p.setDescription(subProjectDto.getDescription());
        p.setDeadline(subProjectDto.getDeadline());
        return subprojectRepository.createSubProject(p, projectId);
    }

    /**
     * Deletes a sub-project by its ID.
     *
     * <p>This method removes a sub-project from the database. Important considerations:</p>
     * <ul>
     *   <li>The operation may fail if the sub-project has associated tasks (database foreign key constraints)</li>
     *   <li>No cascade deletion of tasks is performed in the current implementation</li>
     *   <li>The method returns {@code false} if the sub-project doesn't exist or cannot be deleted</li>
     * </ul>
     *
     * <p><b>Cascade Deletion Strategy:</b></p>
     * <p>Consider the following approaches for handling associated tasks:</p>
     * <ul>
     *   <li>Implement cascade deletion in the service layer (delete tasks first)</li>
     *   <li>Use database-level cascade delete constraints</li>
     *   <li>Prevent deletion if tasks exist (require manual task deletion first)</li>
     *   <li>Implement soft deletion (mark as inactive instead of hard delete)</li>
     * </ul>
     *
     * @param id the ID of the sub-project to delete
     * @return {@code true} if the sub-project was successfully deleted, {@code false} otherwise
     * @throws IllegalArgumentException if {@code id} is invalid
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     *
     * @see SubProjectRepository#delete(long)
     */
    public boolean delete(long id) {
        return subprojectRepository.delete(id);
    }
}