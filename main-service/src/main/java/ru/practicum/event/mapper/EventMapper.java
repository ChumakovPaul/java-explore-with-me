package ru.practicum.event.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
@Component
@RequiredArgsConstructor
public class EventMapper {

    private final CategoryMapper categoryMapper;


    public Event toEvent(NewEventDto newEventDto, User user, Category category) {
        Event event = new Event();
        event.setInitiator(user);
        event.setCategory(category);
        event.setTitle(newEventDto.getTitle());
        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setState(State.PENDING);
        event.setLocation(newEventDto.getLocation());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setPaid(newEventDto.getPaid());
        event.setEventDate(newEventDto.getEventDate());
        event.setCreatedOn(LocalDateTime.now());
        return event;
    }

    public EventFullDto toEventFullDto(Event event, UserShortDto userShortDto) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(categoryMapper.toCategoryDto(event.getCategory()));
        eventFullDto.setConfirmedRequests(1L);
        eventFullDto.setCreatedOn(event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        eventFullDto.setId(event.getId());
        eventFullDto.setInitiator(userShortDto);
        eventFullDto.setLocation(event.getLocation());
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
//        eventFullDto.setPublishedOn(event.getPublishedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState().name());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(1L);
        return eventFullDto;
    }

    public EventShortDto toEventShortDto(Event event, UserShortDto userShortDto) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(categoryMapper.toCategoryDto(event.getCategory()));
        eventShortDto.setConfirmedRequests(1L);
        eventShortDto.setEventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        eventShortDto.setId(event.getId());
        eventShortDto.setInitiator(userShortDto);
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setViews(1L);
        return eventShortDto;
    }
}
