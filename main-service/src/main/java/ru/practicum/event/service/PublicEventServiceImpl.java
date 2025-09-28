package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.StatCreateDto;
import ru.practicum.StatDto;
import ru.practicum.StatsClient;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.PublicGetEventParams;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Sort;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.exception.DateProblemException;
import ru.practicum.request.model.Status;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    final EventRepository eventRepository;
    final EventMapper eventMapper;
    final UserMapper userMapper;
    final StatsClient statsClient;
    final RequestRepository requestRepository;

    @Override
    public List<EventShortDto> getEvents(PublicGetEventParams params, String ip, String uri) {
        String text = Objects.isNull(params.getText()) ? null : params.getText();
        List<Long> categories = params.getCategories() != null ? params.getCategories() : Collections.emptyList();
        Boolean paid = params.getPaid();
        LocalDateTime rangeStart = params.getRangeStart();
        LocalDateTime rangeEnd = params.getRangeEnd();
        if (Objects.isNull(rangeStart)) {
            rangeStart = LocalDateTime.now();
            rangeEnd = rangeStart.plusYears(100);
        }
        if (rangeEnd.isBefore(rangeStart)) {
            throw new DateProblemException("Конец диапазона не может быть раньше начала");
        }
        boolean onlyAvailable = params.isOnlyAvailable();
        Sort sort = params.getSort();
        int pageNumber = params.getFrom() / params.getSize();
        int pageSize = params.getSize();
        List<Event> events = eventRepository
                .getEvents(text,
                        categories,
                        paid,
                        rangeStart,
                        rangeEnd,
                        State.PUBLISHED,
                        PageRequest.of(pageNumber, pageSize))
                .getContent();

        Map<Long, Long> views = getViews(events, rangeStart, rangeEnd);
        for (Long l : views.keySet()) {
            System.out.println("Вот такие просмотры");
            System.out.println(views.get(l));
        }

        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        for (Long l : confirmedRequests.keySet()) {
            System.out.println("Вот такие одобренные заявки");
            System.out.println(confirmedRequests.get(l));
        }
        if (Boolean.TRUE.equals(paid)) {
            events = events.stream().filter(e -> Boolean.TRUE.equals(e.getPaid())).toList();
        } else if (Boolean.FALSE.equals(paid)) {
            events = events.stream().filter(e -> Boolean.FALSE.equals(e.getPaid())).toList();
        }
        if (Boolean.TRUE.equals(onlyAvailable)) {
            events = events.stream().filter(e -> e.getParticipantLimit() > confirmedRequests.get(e.getId())).toList();
        }
        if (Sort.EVENT_DATE.equals(sort)) {
            events.stream().sorted(Comparator.comparing(Event::getEventDate));
        } else if (Sort.VIEWS.equals(sort)) {
            events.stream().sorted(Comparator.comparing(event -> views.getOrDefault(event.getId(), 0L)));
        }
        saveStat(ip, uri);
        return events.stream().map(e ->
                        eventMapper.toEventShortDto(e,
                                userMapper.toUserShortDto(e.getInitiator()),
                                requestRepository.countByEventIdAndStatus(e.getId(), Status.CONFIRMED),
                                views.getOrDefault(e.getId(), 0L))
                )
                .toList();
    }

    @Override
    public EventFullDto getEvent(Long eventId, String ip, String uri) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + "was not found"));
        if (!Objects.equals(event.getState(), State.PUBLISHED)) {
            throw new DataNotFoundException("This event was not be published");
        }
        Map<Long, Long> views = getViews(List.of(event), LocalDateTime.now().minusYears(100), LocalDateTime.now());
        Map<Long, Long> confirmedRequests = getConfirmedRequests(List.of(event));
        EventFullDto result = eventMapper.toEventFullDto(event,
                userMapper.toUserShortDto(event.getInitiator()),
                requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED), views.getOrDefault(event.getId(), 0L));
        result.setConfirmedRequests(confirmedRequests.get(event.getId()));
        result.setViews(views.getOrDefault(event.getId(), 0L));
        result.setViews(result.getViews() == null ? 0 : result.getViews());
        saveStat(ip, uri);
        return result;
    }

    private void saveStat(String ip, String uri) {
        StatCreateDto statCreateDto = new StatCreateDto("ewm-main-service", uri, ip, LocalDateTime.now());
        statsClient.hit(statCreateDto);
    }

    @Override
    public Map<Long, Long> getViews(List<Event> events, LocalDateTime start, LocalDateTime end) {
        String stringStart;
        String stringEnd;
        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .toList();
        for (String s : uris) {
            System.out.println(s);
        }
        if (Objects.isNull(start)) {
            stringStart = LocalDateTime.now().minusYears(100).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            stringEnd = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else {
            stringStart = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            stringEnd = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        Collection<StatDto> views = statsClient.getStat(stringStart, stringEnd, uris, true);
        Map<Long, Long> result = views.stream()
                .collect(Collectors
                        .toMap(statDto -> getEventIdFromDto(statDto),
                                StatDto::getHits));
        return result;

    }

    @Override
    public Map<Long, Long> getConfirmedRequests(List<Event> events) {

        return events
                .stream()
                .collect(Collectors
                        .toMap(event -> event.getId()
                                , event -> requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED)));
    }

    private Long getEventIdFromDto(StatDto statDto) {
        String[] uri = statDto.getUri().split("/");
        if (uri[uri.length - 1].equals("events")) {
            return 0L;
        }
        return Long.parseLong(uri[uri.length - 1]);
    }
}
