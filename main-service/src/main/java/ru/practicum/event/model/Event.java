package ru.practicum.event.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class Event {
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    State state;
    @Embedded
    Location location;
    @Column(name = "participant_limit", nullable = false)
    Long participantLimit;
    @Column(name = "request_moderation", nullable = false)
    Boolean requestModeration;
    @Column(nullable = false)
    Boolean paid;
    @Column(name = "event_date", nullable = false)
    LocalDateTime eventDate;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "created_on", nullable = false)
    LocalDateTime createdOn;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator")
    private User initiator;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categories_id")
    private Category category;
    @Column(length = 120, nullable = false)
    private String title;
    @Column(length = 2000, nullable = false)
    private String annotation;
    @Column(length = 7000, nullable = false)
    private String description;
}