package org.dev.filerouge.service.implementation;
import lombok.RequiredArgsConstructor;
import org.dev.filerouge.domain.Grade;
import org.dev.filerouge.repository.GradeRepository;
import org.dev.filerouge.service.IGradeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GradeService implements IGradeService {
    private final GradeRepository gradeRepository;
    @Override
    public Grade save(Grade grade) {
        return gradeRepository.save(grade);
    }

    @Override
    public Grade update(Grade grade) {
        Optional<Grade> existingGrade = gradeRepository.findById(grade.getId());
        if (existingGrade.isEmpty()) {
            throw new IllegalArgumentException("Grade not found with ID: " + grade.getId());
        }
        return gradeRepository.save(grade);
    }

    @Override
    public Grade findById(UUID id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grade not found with ID: " + id));
    }

    @Override
    public void delete(UUID id) {
        if (!gradeRepository.existsById(id)) {
            throw new IllegalArgumentException("Grade not found with ID: " + id);
        }
        gradeRepository.deleteById(id);
    }

    @Override
    public Page<Grade> findAll(int page, int size) {
        return gradeRepository.findAll(PageRequest.of(page, size));
    }
}
