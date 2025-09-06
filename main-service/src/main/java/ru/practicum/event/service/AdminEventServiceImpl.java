package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.GetEventsParams;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;

    @Override
    public List<EventFullDto> getEvents(GetEventsParams params) {
        List<Long> users = params.getUsers() != null ? params.getUsers() : Collections.emptyList();
        List<State> states = params.getStates() != null ? params.getStates() : Collections.emptyList();
        ;
        List<Long> categories = params.getCategories() != null ? params.getCategories() : Collections.emptyList();
        LocalDateTime rangeStart = params.getRangeStart();
        LocalDateTime rangeEnd = params.getRangeEnd();
        int pageNumber = params.getFrom() / params.getSize();
        int pageSize = params.getSize();
        List<Event> events = eventRepository.getEvents(users
                , states, categories
                , rangeStart, rangeEnd
                , PageRequest.of(pageNumber, pageSize)).getContent();
        return events.stream()
                .map(e -> eventMapper.toEventFullDto(e, userMapper.toUserShortDto(e.getInitiator())))
                .toList();
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getEventDate().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new ForbiddenException("The start date of the event must be at least one hour before the publication date");
        }
        if (event.getState() != State.PENDING) {
            throw new ForbiddenException("An event can only be published if it is in the PENDING state.");
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventAdminRequest.getCategory()).orElseThrow(() -> new DataNotFoundException("Category with id=" + updateEventAdminRequest.getCategory() + "was not found")));
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(updateEventAdminRequest.getLocation());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        if (Objects.equals(updateEventAdminRequest.getStateAction(), StateAction.PUBLISH_EVENT.name())) {
            event.setState(State.PUBLISHED);
        } else if (Objects.equals(updateEventAdminRequest.getStateAction(), StateAction.REJECT_EVENT.name())) {
            if (event.getState() == State.PUBLISHED)
                throw new ForbiddenException("An event can only be canceled if it has not yet been published");
            event.setState(State.CANCELED);
        }
        return eventMapper.toEventFullDto(eventRepository.save(event), userMapper.toUserShortDto(event.getInitiator()));
    }

}

