package ru.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "stat")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(length = 50, nullable = false)
    private String app;
    @Column(length = 50, nullable = false)
    private String uri;
    @Column(length = 15, nullable = false)
    private String ip;
    @Column(name = "time_stamp", nullable = false)
    private LocalDateTime timestamp;
}
