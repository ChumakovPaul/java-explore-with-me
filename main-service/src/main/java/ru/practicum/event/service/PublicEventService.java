package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.PublicGetEventParams;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PublicEventService {
    List<EventShortDto> getEvents(PublicGetEventParams params, String ip, String uri);

    EventFullDto getEvent(Long eventId, String ip, String uri);

    Map<Long, Long> getViews(List<Event> events, LocalDateTime start, LocalDateTime end);

    Map<Long, Long> getConfirmedRequests(List<Event> events);
}
