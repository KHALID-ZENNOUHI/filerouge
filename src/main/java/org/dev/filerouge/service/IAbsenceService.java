package org.dev.filerouge.service;

import org.dev.filerouge.domain.Absence;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public interface IAbsenceService {
    Absence save(Absence absence);

    Absence  update(Absence absence);

    Absence findById(UUID id);

    void delete(UUID id);

    Page<Absence> findAll(int page, int size);
}
