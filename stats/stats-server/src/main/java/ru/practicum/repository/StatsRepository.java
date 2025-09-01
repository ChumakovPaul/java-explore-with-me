package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.StatDto;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stat, Long> {

    @Query("SELECT new ru.practicum.StatDto(stat.app, stat.uri, count(stat.ip)) " +
            "FROM Stat as stat " +
            "WHERE stat.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR stat.uri IN :uris) " +
            "GROUP BY stat.app, stat.uri " +
            "ORDER BY COUNT(stat.ip) DESC"
    )
    List<StatDto> findStats(
            @Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end,
                              @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.StatDto(stat.app, stat.uri, count(DISTINCT stat.ip)) " +
            "FROM Stat as stat " +
            "WHERE stat.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR stat.uri IN :uris) " +
            "GROUP BY stat.app, stat.uri " +
            "ORDER BY COUNT(DISTINCT stat.ip) DESC")
    List<StatDto> findStatsUnique(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end,
                                    @Param("uris") List<String> uris);
}
