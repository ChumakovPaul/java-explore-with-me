package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;
    private final PublicEventService publicEventService;

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new DataNotFoundException("Compilation with id=" + compId + " was not found"));
        Set<Event> events = compilation.getEvents();
        Map<Long, Long> confirmedRequests = publicEventService.getConfirmedRequests(List.copyOf(events));
        Map<Long, Long> views = publicEventService.getViews(List.copyOf(events), null, null);

        return compilationMapper.toCompilationDto(compilation, confirmedRequests, views);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        int pageNumber = from / size;
        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, PageRequest.of(pageNumber, size)).getContent();
        return compilations.stream().map(compilation -> {
            Set<Event> events = compilation.getEvents();
            Map<Long, Long> confirmedRequests = publicEventService.getConfirmedRequests(List.copyOf(events));
            Map<Long, Long> views = publicEventService.getViews(List.copyOf(events), null, null);
            return compilationMapper.toCompilationDto(compilation, confirmedRequests, views);
        }).toList();
    }
}
