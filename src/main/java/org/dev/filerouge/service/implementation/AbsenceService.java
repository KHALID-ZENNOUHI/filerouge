package org.dev.filerouge.service.implementation;

import org.dev.filerouge.domain.Absence;
import org.dev.filerouge.repository.AbsenceRepository;
import org.dev.filerouge.service.IAbsenceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AbsenceService implements IAbsenceService {
    private final AbsenceRepository absenceRepository;

    public AbsenceService(AbsenceRepository absenceRepository) {
        this.absenceRepository = absenceRepository;
    }

    @Override
    public Absence save(Absence absence) {
        return absenceRepository.save(absence);
    }

    @Override
    public Absence update(Absence absence) {
        Optional<Absence> existingAbsence = absenceRepository.findById(absence.getId());
        if (existingAbsence.isEmpty()) {
            throw new IllegalArgumentException("Absence not found with ID: " + absence.getId());
        }
        return absenceRepository.save(absence);
    }

    @Override
    public Absence findById(UUID id) {
        return absenceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Absence not found with ID: " + id));
    }

    @Override
    public void delete(UUID id) {
        if (!absenceRepository.existsById(id)) {
            throw new IllegalArgumentException("Absence not found with ID: " + id);
        }
        absenceRepository.deleteById(id);
    }

    @Override
    public Page<Absence> findAll(int page, int size) {
        return absenceRepository.findAll(PageRequest.of(page, size));
    }
}
