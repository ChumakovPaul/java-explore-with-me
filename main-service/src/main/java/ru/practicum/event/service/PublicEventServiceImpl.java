package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.StatCreateDto;
import ru.practicum.StatsClient;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.PublicGetEventParams;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Sort;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    final EventRepository eventRepository;
    final EventMapper eventMapper;
    final UserMapper userMapper;
    final StatsClient statsClient;

    @Override
    public List<EventShortDto> getEvents(PublicGetEventParams params, String ip, String uri) {
        String text = params.getText().isBlank() ? null : params.getText();
        List<Long> categories = params.getCategories() != null ? params.getCategories() : Collections.emptyList();
        Boolean paid = params.getPaid();
        LocalDateTime rangeStart = params.getRangeStart();
        LocalDateTime rangeEnd = params.getRangeEnd();
        if (rangeEnd.isBefore(rangeStart)) {
            throw new ForbiddenException("Конец диапазона не может быть раньше начала");
        }
        if (Objects.isNull(rangeStart)) {
            rangeStart = LocalDateTime.now();
            rangeEnd = LocalDateTime.MAX;
        }
        boolean onlyAvailable = params.isOnlyAvailable();
        Sort sort = params.getSort();
        int pageNumber = params.getFrom() / params.getSize();
        int pageSize = params.getSize();
        List<Event> events = eventRepository.getEvents(text, categories, paid, rangeStart, rangeEnd, State.PUBLISHED, PageRequest.of(pageNumber, pageSize)).getContent();

        saveStat(ip, uri);


        return events.stream().map(e -> eventMapper.toEventShortDto(e, userMapper.toUserShortDto(e.getInitiator()))).toList();
    }

    public void saveStat(String ip, String uri) {
        StatCreateDto statCreateDto = new StatCreateDto("ewm-main-service", uri, ip, LocalDateTime.now());
        statsClient.hit(statCreateDto);
    }
}
