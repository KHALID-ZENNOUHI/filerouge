package org.dev.filerouge.service;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Base service interface that defines common CRUD operations
 * for all domain entities.
 *
 * @param <T> the entity type
 */
public interface BaseService<T> {

    /**
     * Saves a new entity
     *
     * @param entity the entity to save
     * @return the saved entity with its ID populated
     */
    T save(T entity);

    /**
     * Updates an existing entity
     *
     * @param entity the entity to update
     * @return the updated entity
     */
    T update(T entity);

    /**
     * Finds an entity by its ID
     *
     * @param id the ID of the entity to find
     * @return the found entity
     */
    T findById(UUID id);

    /**
     * Deletes an entity by its ID
     *
     * @param id the ID of the entity to delete
     */
    void delete(UUID id);

    /**
     * Finds all entities with pagination
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of entities
     */
    Page<T> findAll(int page, int size);

    /**
     * Finds all entities
     *
     * @return a list of all entities
     */
    List<T> findAll();

    /**
     * Checks if an entity exists by its ID
     *
     * @param id the ID to check
     * @return true if the entity exists, false otherwise
     */
    boolean existsById(UUID id);
}