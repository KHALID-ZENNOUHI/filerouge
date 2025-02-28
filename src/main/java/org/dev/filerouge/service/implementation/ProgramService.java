package org.dev.filerouge.service.implementation;

import lombok.RequiredArgsConstructor;
import org.dev.filerouge.domain.Program;
import org.dev.filerouge.repository.ProgramRepository;
import org.dev.filerouge.service.IProgramService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramService implements IProgramService {

    private final ProgramRepository programRepository;

    @Override
    public Program save(Program program) {
        return programRepository.save(program);
    }

    @Override
    public Program update(Program program) {
        if (!programRepository.existsById(program.getId())) {
            throw new RuntimeException("Program not found with ID: " + program.getId());
        }
        return programRepository.save(program);
    }

    @Override
    public Program findById(UUID id) {
        return programRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Program not found with ID: " + id));
    }

    @Override
    public void delete(UUID id) {
        if (!programRepository.existsById(id)) {
            throw new RuntimeException("Program not found with ID: " + id);
        }
        programRepository.deleteById(id);
    }

    @Override
    public Page<Program> findAll(int page, int size) {
        return programRepository.findAll(PageRequest.of(page, size));
    }
}
