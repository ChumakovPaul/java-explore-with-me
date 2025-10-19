package ru.practicum.compilation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.compilation.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("SELECT c FROM Compilation c WHERE (?1 IS NULL OR c.pinned = ?1)")
    Page<Compilation> findAllByPinned(Boolean pinned, Pageable pageable);
}
