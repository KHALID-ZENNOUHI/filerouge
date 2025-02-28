package org.dev.filerouge.web.api;

import lombok.RequiredArgsConstructor;
import org.dev.filerouge.domain.Class;
import org.dev.filerouge.service.IClassService;
import org.dev.filerouge.service.implementation.ClassService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassController {
    private final IClassService classService;

    @PostMapping
    public ResponseEntity<Class> createClass(@RequestBody Class aClass) {
        return ResponseEntity.ok(classService.save(aClass));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Class> updateClass(@PathVariable UUID id, @RequestBody Class aClass) {
        aClass.setId(id);
        return ResponseEntity.ok(classService.update(aClass));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Class> getClassById(@PathVariable UUID id) {
        return ResponseEntity.ok(classService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable UUID id) {
        classService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<Class>> getAllClasses(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(classService.findAll(page, size));
    }
}
