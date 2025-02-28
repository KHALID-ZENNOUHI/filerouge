package org.dev.filerouge.service.implementation;

import lombok.RequiredArgsConstructor;
import org.dev.filerouge.domain.Session;
import org.dev.filerouge.repository.SessionRepository;
import org.dev.filerouge.service.ISessionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService implements ISessionService {
    private final SessionRepository sessionRepository;

    @Override
    public Session save(Session session) {
        return sessionRepository.save(session);
    }

    @Override
    public Session update(Session session) {
        Optional<Session> existingSession = sessionRepository.findById(session.getId());
        if (existingSession.isEmpty()) {
            throw new IllegalArgumentException("Session not found with ID: " + session.getId());
        }
        return sessionRepository.save(session);
    }

    @Override
    public Session findById(UUID id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with ID: " + id));
    }

    @Override
    public void delete(UUID id) {
        if (!sessionRepository.existsById(id)) {
            throw new IllegalArgumentException("Session not found with ID: " + id);
        }
        sessionRepository.deleteById(id);
    }

    @Override
    public Page<Session> findAll(int page, int size) {
        return sessionRepository.findAll(PageRequest.of(page, size));
    }
}
