package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final PublicEventService publicEventService;

    @Override
    public EventFullDto save(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() -> new DataNotFoundException("Category with id=" + newEventDto.getCategory() + " was not found"));
        Event event = eventRepository.save(eventMapper.toEvent(newEventDto, user, category));
        Map<Long, Long> views = publicEventService.getViews(List.of(event), null, null);
        return eventMapper.toEventFullDto(event,
                userMapper.toUserShortDto(user),
                requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED),
                views.getOrDefault(event.getId(), 0L));
    }

    @Override
    public List<EventShortDto> getEvents(Long userId, int from, int size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        List<Event> events = eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size))
                .getContent();
        Map<Long, Long> views = publicEventService.getViews(events, null, null);

        return events.stream()
                .map(event -> eventMapper.toEventShortDto(event,
                        userMapper.toUserShortDto(user),
                        requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED),
                        views.getOrDefault(event.getId(), 0L)
                ))
                .toList();
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        UserShortDto user = userMapper.toUserShortDto(userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found")));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        Map<Long, Long> views = publicEventService.getViews(List.of(event), null, null);
        return eventMapper.toEventFullDto(event, user, requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED), views.getOrDefault(event.getId(), 0L));
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + ", where initiator is user id=" + userId + "was not found"));
        if (updateEventUserRequest.getEventDate() != null && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("The date and time of the event cannot be earlier than two hours from the current moment");
        }
        if (event.getState() != State.PENDING && event.getState() != State.CANCELED) {
            throw new ForbiddenException("You can only change a cancelled or pending event.");
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventUserRequest.getCategory()).orElseThrow(() -> new DataNotFoundException("Category with id=" + updateEventUserRequest.getCategory() + "was not found")));
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(updateEventUserRequest.getLocation());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        if (Objects.equals(updateEventUserRequest.getStateAction(), StateAction.CANCEL_REVIEW.name())) {
            event.setState(State.CANCELED);
        } else if (Objects.equals(updateEventUserRequest.getStateAction(), StateAction.SEND_TO_REVIEW.name())) {
            event.setState(State.PENDING);
        }
        Map<Long, Long> views = publicEventService.getViews(List.of(event), null, null);
        return eventMapper.toEventFullDto(eventRepository.save(event),
                userMapper.toUserShortDto(event.getInitiator()),
                requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED), views.getOrDefault(event.getId(), 0L));
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + ", where initiator is user id=" + userId + "was not found"));
        List<Request> requests = requestRepository.findByEventId(eventId);
        return requests.stream().map(requestMapper::toParticipationRequestDto).toList();
    }

    @Override
    public EventRequestStatusUpdateRequest updateRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + ", where initiator is user id=" + userId + "was not found"));
        List<Long> requests = new ArrayList<>();
        Long confirmedRequests = requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED);
        if (eventRequestStatusUpdateRequest.getStatus().equals(Status.REJECTED.name())) {
            for (Long requestId : eventRequestStatusUpdateRequest.getRequestIds()) {
                Request request = requestRepository.findById(requestId).orElseThrow(() -> new DataNotFoundException("Request with id=" + requestId + " was not found"));
                if (request.getStatus().equals(Status.CONFIRMED)) {
                    throw new ForbiddenException("You cannot cancel an already accepted event registration request");
                }
                request.setStatus(Status.valueOf(eventRequestStatusUpdateRequest.getStatus()));
                requestRepository.save(request);
                requests.add(request.getId());
            }
        }
        if (eventRequestStatusUpdateRequest.getStatus().equals(Status.CONFIRMED.name())) {
            for (Long requestId : eventRequestStatusUpdateRequest.getRequestIds()) {
                Request request = requestRepository.findById(requestId).orElseThrow(() -> new DataNotFoundException("Request with id=" + requestId + " was not found"));
                if (event.getParticipantLimit() > confirmedRequests) {
                    request.setStatus(Status.valueOf(eventRequestStatusUpdateRequest.getStatus()));
                    requestRepository.save(request);
                    requests.add(request.getId());
                } else {
                    throw new ForbiddenException("The participant limit has been reached");
                }
            }
        }
        return new EventRequestStatusUpdateRequest(requests, eventRequestStatusUpdateRequest.getStatus());
    }
}