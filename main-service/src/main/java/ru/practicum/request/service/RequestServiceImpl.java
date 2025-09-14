package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ForbiddenException("Нельзя добавить повторный запрос.");
        }
        if (event.getInitiator().getId() == userId) {
            throw new ForbiddenException("Инициатор события не может добавить запрос на участие в своём событии.");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ForbiddenException("Нельзя участвовать в неопубликованном или отмененном событии.");
        }
        if (event.getParticipantLimit() != 0 && requestRepository.countByEventIdAndStatus(eventId, Status.CONFIRMED) >= event.getParticipantLimit()) {
            throw new ForbiddenException("У события достигнут лимит запросов на участие.");
        }
        Request request = new Request();
        request.setRequester(user);
        request.setEvent(event);
        request.setStatus(Status.PENDING);
        request.setCreated(LocalDateTime.now());
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
        }
        Request result = requestRepository.save(request);
        return requestMapper.toParticipationRequestDto(result);
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        List<Request> requests = requestRepository.findByRequesterId(userId);

        return requests.stream().map(requestMapper::toParticipationRequestDto).toList();
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User with id=" + userId + " was not found"));
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(() -> new DataNotFoundException("Request with id=" + requestId + " was not found"));
        request.setStatus(Status.CANCELED);
        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

}
