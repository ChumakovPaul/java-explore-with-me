package ru.practicum;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsClientImpl implements StatsClient {
    private final RestClient restClient;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClientImpl(@Value("${explore-with-me.stats-server.url:http://localhost:9090}") String clientUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(clientUrl)
                .build();
    }

    @Override
    public void hit(StatCreateDto statCreateDto) {
        restClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(statCreateDto)
                .retrieve()
                .toBodilessEntity();
        log.info("Saved hit: {}", statCreateDto);
    }


    @Override
    public Collection<StatDto> getStat(String start, String end, List<String> uris, Boolean unique) {
        validateRange(start, end);
        Collection<StatDto> stats = restClient.get()
                .uri(uriBuilder -> uriGetStats(uriBuilder, start, end, uris, unique))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        log.info("Getting stats (start={}, end={}, urisCount={}, unique={})",
                start, end, uris == null ? 0 : uris.size(), unique);
        return stats;
    }

    private URI uriGetStats(UriBuilder uriBuilder, String start, String end, List<String> uris, Boolean unique) {
        UriBuilder builder = uriBuilder.path("/stats")
                .queryParam("start", start)
                .queryParam("end", end);
        if (uris != null && !uris.isEmpty()) {
            uris.forEach(url -> builder.queryParam("uris", url));
        }
        if (unique != null) {
            builder.queryParam("unique", unique);
        }
        log.info("Built URI for stats (start={}, end={}, urisCount={}, unique={})",
                start, end, uris == null ? 0 : uris.size(), unique);
        return builder.build();
    }

    private void validateRange(String start, String end) {
        if (start == null || end == null) {
            log.warn("Range contains null value: start={}, end={}", start, end);
            throw new IllegalArgumentException("Range must not contain null");
        }

        LocalDateTime startDt = LocalDateTime.parse(start, FORMATTER);
        LocalDateTime endDt = LocalDateTime.parse(end, FORMATTER);

        if (startDt.isAfter(endDt)) {
            log.warn("Invalid range: start={} is after end={}", start, end);
            throw new IllegalArgumentException("Invalid range: start is after end");
        }
    }
}
