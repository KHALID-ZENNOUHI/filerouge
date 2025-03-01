package org.dev.filerouge.service.implementation;

import jakarta.transaction.Transactional;
import org.dev.filerouge.service.BaseService;
import org.dev.filerouge.web.error.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Abstract base implementation of {@link BaseService} providing generic implementations
 * for common CRUD operations.
 *
 * @param <T> the entity type
 * @param <R> the repository type
 */
@Transactional
public abstract class BaseServiceImpl<T, R extends JpaRepository<T, UUID>> implements BaseService<T> {

    protected final R repository;
    private final String entityName;

    protected BaseServiceImpl(R repository, String entityName) {
        this.repository = repository;
        this.entityName = entityName;
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public T update(T entity) {
        // Entity existence validation is left to specific service implementations
        // since we need the entity ID which is type-specific
        return repository.save(entity);
    }

    @Override
    public T findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException(entityName, "id", id));
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ServiceException.ResourceNotFoundException(entityName, "id", id);
        }
        repository.deleteById(id);
    }

    @Override
    public Page<T> findAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }
}