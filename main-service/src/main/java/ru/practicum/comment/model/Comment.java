package ru.practicum.comment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(length = 2000, nullable = false)
    private String text;
    @Column(name = "event_id", nullable = false)
    private long eventId;
    @Column(name = "author_id", nullable = false)
    private long authorId;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime created;
    @Column(name = "updated_on")
    private LocalDateTime updated;
}
