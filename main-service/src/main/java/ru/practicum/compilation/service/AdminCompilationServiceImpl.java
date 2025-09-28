package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.PublicEventService;
import ru.practicum.exception.DataNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final CompilationRepository compilationRepository;
    private final PublicEventService publicEventService;


    @Override
    public CompilationDto save(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getEvents() == null) {
            newCompilationDto.setEvents(Set.of());
        }
        Set<Event> events = newCompilationDto.getEvents()
                .stream()
                .map(id -> eventRepository.findById(id)
                        .orElseThrow(() -> new DataNotFoundException("Event with id=" + id + " was not found")))
                .collect(Collectors.toSet());
        Map<Long, Long> confirmedRequests = publicEventService.getConfirmedRequests(List.copyOf(events));
        Map<Long, Long> views = publicEventService.getViews(List.copyOf(events), null, null);
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        Compilation compilation = compilationRepository.save(compilationMapper.toCompilation(newCompilationDto, events));
        return compilationMapper.toCompilationDto(compilation, confirmedRequests, views);
    }

    @Override
    public void delete(Long compId) {
            Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new DataNotFoundException("Compilation with id=" + compId + " was not found"));
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new DataNotFoundException("Compilation with id=" + compId + " was not found"));
        if (updateCompilationRequest.getEvents() != null) {
            Set<Event> events = updateCompilationRequest.getEvents()
                    .stream()
                    .map(id -> eventRepository.findById(id)
                            .orElseThrow(() -> new DataNotFoundException("Event with id=" + id + " was not found")))
                    .collect(Collectors.toSet());
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        Map<Long, Long> confirmedRequests = publicEventService.getConfirmedRequests(List.copyOf(compilation.getEvents()));
        Map<Long, Long> views = publicEventService.getViews(List.copyOf(compilation.getEvents()), null, null);
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation), confirmedRequests, views);
    }

}
