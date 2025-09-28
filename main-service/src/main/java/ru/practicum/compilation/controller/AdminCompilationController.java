package ru.practicum.compilation.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.AdminCompilationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "admin/compilations")
public class AdminCompilationController {

    private final AdminCompilationService adminCompilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompillation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Start creating event Compilation: {}", newCompilationDto);
        CompilationDto compilationDto = adminCompilationService.save(newCompilationDto);
        log.info("Finish creating event Compilation: {}", compilationDto);
        return compilationDto;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Positive Long compId) {
        log.info("Delete event Compilation id={}", compId);
        adminCompilationService.delete(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@PathVariable @Positive Long compId,
                                            @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        log.info("Update event Compilation id={}: {}", compId, updateCompilationRequest);
        return adminCompilationService.update(compId, updateCompilationRequest);
    }
}
