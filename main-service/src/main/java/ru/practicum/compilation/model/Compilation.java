package ru.practicum.compilation.model;


import jakarta.persistence.*;
import lombok.*;
import ru.practicum.event.model.Event;

import java.util.Set;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Boolean pinned;
    @Column(length = 50, nullable = false)
    private String title;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilations_id"),
            inverseJoinColumns = @JoinColumn(name = "events_id"))
    private Set<Event> events;
}
