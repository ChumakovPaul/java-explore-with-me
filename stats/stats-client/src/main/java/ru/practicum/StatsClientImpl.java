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
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClientImpl(@Value("${stats-server.url:http://localhost:9090}") String clientUrl) {
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
        log.info("сохранили информацию что был запрос");
    }


    @Override
    public Collection<StatDto> getStat(String start, String end, List<String> uris, Boolean unique) {
        if (start == null || end == null) {
            log.warn("диапазон не может содержать null");
            throw new IllegalArgumentException("диапазон не может содержать null");
        }
        LocalDateTime startDataTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endDataTime = LocalDateTime.parse(end, formatter);
        if (startDataTime.isAfter(endDataTime)) {
            log.warn("задан не верный диапазон");
            throw new IllegalArgumentException("задан не верный диапазон");
        }
        Collection<StatDto> stats = restClient.get()
                .uri(uriBuilder -> uriGetStats(uriBuilder, start, end, uris, unique))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        log.info("запрос с параметрами");
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
        log.info("конвертировали url + параметры");
        return builder.build();
    }
}
