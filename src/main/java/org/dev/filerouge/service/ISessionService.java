package org.dev.filerouge.service;

import org.dev.filerouge.domain.Session;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ISessionService {
    Session save(Session session);

    Session update(Session session);

    Session findById(UUID id);

    void delete(UUID id);

    Page<Session> findAll(int page, int size);
}
