package org.dev.filerouge.service.implementation;

import lombok.RequiredArgsConstructor;
import org.dev.filerouge.domain.Subject;
import org.dev.filerouge.repository.SubjectRepository;
import org.dev.filerouge.service.ISubjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectService implements ISubjectService {
    private final SubjectRepository subjectRepository;

    @Override
    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }

    @Override
    public Subject update(Subject subject) {
        Optional<Subject> existingSubject = subjectRepository.findById(subject.getId());
        if (existingSubject.isEmpty()) {
            throw new IllegalArgumentException("Subject not found with ID: " + subject.getId());
        }
        return subjectRepository.save(subject);
    }

    @Override
    public Subject findById(UUID id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found with ID: " + id));
    }

    @Override
    public void delete(UUID id) {
        if (!subjectRepository.existsById(id)) {
            throw new IllegalArgumentException("Subject not found with ID: " + id);
        }
        subjectRepository.deleteById(id);
    }

    @Override
    public Page<Subject> findAll(int page, int size) {
        return subjectRepository.findAll(PageRequest.of(page, size));
    }
}

