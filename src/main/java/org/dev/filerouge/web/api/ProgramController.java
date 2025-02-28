package org.dev.filerouge.controller;

import lombok.RequiredArgsConstructor;
import org.dev.filerouge.domain.Program;
import org.dev.filerouge.service.IProgramService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/programs")
public class ProgramController {

    private final IProgramService programService;

    @PostMapping
    public ResponseEntity<Program> createProgram(@RequestBody Program program) {
        Program savedProgram = programService.save(program);
        return ResponseEntity.ok(savedProgram);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Program> updateProgram(@PathVariable UUID id, @RequestBody Program program) {
        program.setId(id); // Ensure the ID matches the path variable
        Program updatedProgram = programService.update(program);
        return ResponseEntity.ok(updatedProgram);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Program> getProgramById(@PathVariable UUID id) {
        Program program = programService.findById(id);
        return ResponseEntity.ok(program);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgram(@PathVariable UUID id) {
        programService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<Program>> getAllPrograms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Program> programs = programService.findAll(page, size);
        return ResponseEntity.ok(programs);
    }
}
