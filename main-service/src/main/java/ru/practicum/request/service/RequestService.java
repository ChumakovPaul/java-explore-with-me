package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
        ParticipationRequestDto addRequest(Long userId,Long eventId);

        List<ParticipationRequestDto> getRequests(Long userId);

        ParticipationRequestDto cancelRequest(Long userId, Long eventId);
}
