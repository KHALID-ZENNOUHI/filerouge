package org.dev.filerouge.web.api;

import org.dev.filerouge.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * Base REST controller that provides standard CRUD operations for entities.
 *
 * @param <T> The entity type
 * @param <S> The service type
 */
public abstract class BaseController<T, S extends BaseService<T>> {

    protected final S service;

    protected BaseController(S service) {
        this.service = service;
    }

    /**
     * Creates a new entity.
     *
     * @param entity The entity to create
     * @return The created entity with HTTP 201 Created status
     */
    @PostMapping
    public ResponseEntity<T> create(@Valid @RequestBody T entity) {
        T savedEntity = service.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEntity);
    }

    /**
     * Updates an existing entity.
     *
     * @param id The ID of the entity to update
     * @param entity The updated entity data
     * @return The updated entity with HTTP 200 OK status
     */
    @PutMapping("/{id}")
    public ResponseEntity<T> update(@PathVariable UUID id, @Valid @RequestBody T entity) {
        // The setId method must be implemented in controllers that extend this class
        setId(entity, id);
        T updatedEntity = service.update(entity);
        return ResponseEntity.ok(updatedEntity);
    }

    /**
     * Retrieves an entity by its ID.
     *
     * @param id The ID of the entity to retrieve
     * @return The entity with HTTP 200 OK status
     */
    @GetMapping("/{id}")
    public ResponseEntity<T> getById(@PathVariable UUID id) {
        T entity = service.findById(id);
        return ResponseEntity.ok(entity);
    }

    /**
     * Deletes an entity by its ID.
     *
     * @param id The ID of the entity to delete
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a paginated list of entities.
     *
     * @param page The page number (0-indexed)
     * @param size The page size
     * @return A page of entities with HTTP 200 OK status
     */
    @GetMapping
    public ResponseEntity<Page<T>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<T> entities = service.findAll(page, size);
        return ResponseEntity.ok(entities);
    }

    /**
     * Retrieves all entities without pagination.
     *
     * @return A list of all entities with HTTP 200 OK status
     */
    @GetMapping("/all")
    public ResponseEntity<List<T>> getAllNoPaging() {
        List<T> entities = service.findAll();
        return ResponseEntity.ok(entities);
    }

    /**
     * Checks if an entity with the given ID exists.
     *
     * @param id The ID to check
     * @return true if the entity exists, false otherwise with HTTP 200 OK status
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> exists(@PathVariable UUID id) {
        boolean exists = service.existsById(id);
        return ResponseEntity.ok(exists);
    }

    /**
     * Sets the ID of an entity. This method must be implemented by subclasses.
     *
     * @param entity The entity
     * @param id The ID to set
     */
    protected abstract void setId(T entity, UUID id);
}