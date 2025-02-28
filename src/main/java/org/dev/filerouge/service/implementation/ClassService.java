package org.dev.filerouge.service.implementation;

import lombok.RequiredArgsConstructor;
import org.dev.filerouge.domain.Class;
import org.dev.filerouge.repository.ClassRepository;
import org.dev.filerouge.service.IClassService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassService implements IClassService {
    private final ClassRepository classRepository;

    @Override
    public Class save(Class aClass) {
        return classRepository.save(aClass);
    }

    @Override
    public Class update(Class aClass) {
        Optional<Class> existingClass = classRepository.findById(aClass.getId());
        if (existingClass.isEmpty()) {
            throw new IllegalArgumentException("Class not found with ID: " + aClass.getId());
        }
        return classRepository.save(aClass);
    }

    @Override
    public Class findById(UUID id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Class not found with ID: " + id));
    }

    @Override
    public void delete(UUID id) {
        if (!classRepository.existsById(id)) {
            throw new IllegalArgumentException("Class not found with ID: " + id);
        }
        classRepository.deleteById(id);
    }

    @Override
    public Page<Class> findAll(int page, int size) {
        return classRepository.findAll(PageRequest.of(page, size));
    }
}
