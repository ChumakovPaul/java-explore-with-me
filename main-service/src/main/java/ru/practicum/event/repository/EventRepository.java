package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    boolean existsByCategoryId(Long catId);

    Page<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);


    @Query("SELECT e FROM Event e " +
            "WHERE (?1 is null or e.initiator.id in ?1) " +
            "  AND (?2 is null or e.state in ?2) " +
            "  AND (?3 is null or e.category.id in ?3) " +
            "  AND (e.eventDate >= ?4) " +
            "  AND (e.eventDate <= ?5)")
    Page<Event> getEvents(List<Long> users,
                          List<State> states,
                          List<Long> categories,
                          LocalDateTime rangeStart,
                          LocalDateTime rangeEnd,
                          Pageable pageable);

}
