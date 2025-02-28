package org.dev.filerouge.web.api;

import lombok.RequiredArgsConstructor;
import org.dev.filerouge.domain.Session;
import org.dev.filerouge.service.ISessionService;
import org.dev.filerouge.service.implementation.SessionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final ISessionService sessionService;

    @PostMapping
    public ResponseEntity<Session> createSession(@RequestBody Session session) {
        return ResponseEntity.ok(sessionService.save(session));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Session> updateSession(@PathVariable UUID id, @RequestBody Session session) {
        session.setId(id);
        return ResponseEntity.ok(sessionService.update(session));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Session> getSessionById(@PathVariable UUID id) {
        return ResponseEntity.ok(sessionService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable UUID id) {
        sessionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<Session>> getAllSessions(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(sessionService.findAll(page, size));
    }
}
