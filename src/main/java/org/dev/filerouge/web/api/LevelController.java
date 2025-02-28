package org.dev.filerouge.web.api;

import lombok.RequiredArgsConstructor;
import org.dev.filerouge.domain.Level;
import org.dev.filerouge.service.ILevelService;
import org.dev.filerouge.service.implementation.LevelService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/levels")
@RequiredArgsConstructor
public class LevelController {
    private final ILevelService levelService;

    @PostMapping
    public ResponseEntity<Level> createLevel(@RequestBody Level level) {
        return ResponseEntity.ok(levelService.save(level));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Level> updateLevel(@PathVariable UUID id, @RequestBody Level level) {
        level.setId(id);
        return ResponseEntity.ok(levelService.update(level));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Level> getLevelById(@PathVariable UUID id) {
        return ResponseEntity.ok(levelService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLevel(@PathVariable UUID id) {
        levelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<Level>> getAllLevels(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(levelService.findAll(page, size));
    }
}
