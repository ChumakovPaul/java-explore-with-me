package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationService {

    CompilationDto getCompilation(Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);
}
