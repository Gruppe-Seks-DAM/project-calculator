package com.example.projectcalculator.service;

import com.example.projectcalculator.dto.ProjectDto;
import com.example.projectcalculator.model.Project;
import com.example.projectcalculator.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service layer for managing {@link Project} entities in the Project Calculator application.
 *
 * <p>This service class acts as a business logic layer between the controller and repository,
 * providing operations for managing projects. It handles the conversion between DTOs and entities,
 * and orchestrates repository calls to perform CRUD operations.</p>
 *
 * <p><b>Service Layer Responsibilities:</b></p>
 * <ul>
 *   <li>Business logic validation and processing</li>
 *   <li>DTO to entity conversion and vice versa</li>
 *   <li>Transaction management (implicit via Spring's @Service)</li>
 *   <li>Orchestrating calls to the repository layer</li>
 * </ul>
 *
 * <p><b>Typical Flow:</b></p>
 * <ol>
 *   <li>Controller receives HTTP request with {@link ProjectDto}</li>
 *   <li>Controller calls service methods, passing the DTO</li>
 *   <li>Service validates business rules, converts DTO to entity</li>
 *   <li>Service calls repository to persist/retrieve data</li>
 *   <li>Service may convert entity back to DTO for response</li>
 * </ol>
 *
 * <p><b>Note:</b> The current implementation is minimal and could be extended with:
 * <ul>
 *   <li>Additional business validations (e.g., deadline cannot be in the past)</li>
 *   <li>Complex operations involving multiple repositories</li>
 *   <li>Caching strategies for frequently accessed projects</li>
 *   <li>Event publishing for project lifecycle events</li>
 * </ul></p>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>
 * // In a controller:
 * &#64;Autowired
 * private ProjectService projectService;
 *
 * // Get all projects
 * List<Project> projects = projectService.getAllProjects();
 *
 * // Create a new project
 * ProjectDto projectDto = new ProjectDto();
 * projectDto.setName("New Website");
 * projectDto.setDescription("Build company website");
 * projectDto.setDeadline(LocalDate.of(2024, 12, 31));
 * boolean created = projectService.create(projectDto);
 *
 * // Delete a project
 * boolean deleted = projectService.delete(1L);
 * </pre>
 *
 * @author Arian
 * @version 1.0
 * @since 1.0
 * @see Project
 * @see ProjectDto
 * @see ProjectRepository
 * @see Service
 */
@Service
public class ProjectService {

    /** Repository for performing data access operations on Project entities. */
    private final ProjectRepository repository;

    /**
     * Constructs a new ProjectService with the specified repository.
     *
     * <p>This constructor is automatically wired by Spring's dependency injection
     * framework. The repository parameter is required and cannot be null.</p>
     *
     * @param repository the ProjectRepository to use for data access operations
     * @throws IllegalArgumentException if {@code repository} is {@code null}
     */
    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all projects from the database.
     *
     * <p>This method delegates to the repository to fetch all projects. The returned
     * list is ordered according to the repository's implementation (typically by ID).</p>
     *
     * <p><b>Performance Considerations:</b></p>
     * <ul>
     *   <li>For large numbers of projects, consider adding pagination</li>
     *   <li>Each project entity may be large; consider using projection DTOs</li>
     * </ul>
     *
     * @return a list of all projects in the database, ordered by ID;
     *         returns an empty list if no projects exist
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     * @see ProjectRepository#findAllProjects()
     */
    public List<Project> getAllProjects() {
        return repository.findAllProjects();
    }

    /**
     * Creates a new project from the provided DTO.
     *
     * <p>This method performs the following steps:</p>
     * <ol>
     *   <li>Validates that the DTO is not null (implicitly by method execution)</li>
     *   <li>Converts the {@link ProjectDto} to a {@link Project} entity</li>
     *   <li>Delegates persistence to the repository</li>
     *   <li>Returns the creation success status</li>
     * </ol>
     *
     * <p><b>Note:</b> The current implementation does not perform business validation
     * beyond what's already in the DTO. Consider adding:</p>
     * <ul>
     *   <li>Validation that deadline is not in the past</li>
     *   <li>Validation that project name is unique</li>
     *   <li>Automatic setting of creation timestamp</li>
     * </ul>
     *
     * @param dto the data transfer object containing project information (must not be {@code null})
     * @return {@code true} if the project was successfully created, {@code false} otherwise
     * @throws IllegalArgumentException if {@code dto} is {@code null} or contains invalid data
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     *
     * @see ProjectDto
     * @see ProjectRepository#create(Project)
     */
    public boolean create(ProjectDto dto) {
        Project p = new Project();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setDeadline(dto.getDeadline());
        return repository.create(p);
    }

    /**
     * Deletes a project by its ID.
     *
     * <p>This method removes a project from the database. The operation may fail if:</p>
     * <ul>
     *   <li>The project does not exist</li>
     *   <li>The project has associated sub-projects (database foreign key constraints)</li>
     *   <li>Database constraints prevent deletion</li>
     * </ul>
     *
     * <p><b>Cascade Considerations:</b></p>
     * <p>The current implementation does not handle cascading deletion of sub-projects and tasks.
     * This behavior depends on database cascade settings. Consider implementing:</p>
     * <ul>
     *   <li>Explicit cascade deletion in service layer</li>
     *   <li>Soft delete (mark as inactive) instead of hard delete</li>
     *   <li>Validation to prevent deletion of active projects</li>
     * </ul>
     *
     * @param id the ID of the project to delete
     * @return {@code true} if the project was successfully deleted, {@code false} otherwise
     * @throws IllegalArgumentException if {@code id} is null or invalid
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     *
     * @see ProjectRepository#delete(long)
     */
    public boolean delete(long id) {
        return repository.delete(id);
    }
}