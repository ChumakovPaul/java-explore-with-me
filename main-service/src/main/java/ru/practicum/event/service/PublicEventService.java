package ru.practicum.event.service;

import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.PublicGetEventParams;

import java.util.List;

public interface PublicEventService {
    List<EventShortDto> getEvents(PublicGetEventParams params, String ip, String uri);
}
