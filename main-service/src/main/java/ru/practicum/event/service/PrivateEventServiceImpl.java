package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
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

    @Override
    public EventFullDto save(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() -> new DataNotFoundException("Category with id=" + newEventDto.getCategory() + " was not found"));
        Event event = eventRepository.save(eventMapper.toEvent(newEventDto, user, category));
        return eventMapper.toEventFullDto(event, userMapper.toUserShortDto(user));
    }

    @Override
    public List<EventShortDto> getEvents(Long userId, int from, int size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        return eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size)).stream().map(event -> eventMapper.toEventShortDto(event, userMapper.toUserShortDto(user))).toList();
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        UserShortDto user = userMapper.toUserShortDto(userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found")));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        return eventMapper.toEventFullDto(event, user);
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

        return eventMapper.toEventFullDto(eventRepository.save(event),userMapper.toUserShortDto(event.getInitiator()));
}}

