package org.dev.filerouge.web.api;

import lombok.RequiredArgsConstructor;
import org.dev.filerouge.domain.Absence;
import org.dev.filerouge.service.IAbsenceService;
import org.dev.filerouge.service.implementation.AbsenceService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/absences")
@RequiredArgsConstructor
public class AbsenceController {
    private final IAbsenceService absenceService;

    @PostMapping
    public ResponseEntity<Absence> createAbsence(@RequestBody Absence absence) {
        return ResponseEntity.ok(absenceService.save(absence));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Absence> updateAbsence(@PathVariable UUID id, @RequestBody Absence absence) {
        absence.setId(id);
        return ResponseEntity.ok(absenceService.update(absence));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Absence> getAbsenceById(@PathVariable UUID id) {
        return ResponseEntity.ok(absenceService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbsence(@PathVariable UUID id) {
        absenceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<Absence>> getAllAbsences(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(absenceService.findAll(page, size));
    }
}
