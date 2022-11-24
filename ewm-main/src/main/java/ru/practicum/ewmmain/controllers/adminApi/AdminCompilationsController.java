package ru.practicum.ewmmain.controllers.adminApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.dto.compilation.CompilationDto;
import ru.practicum.ewmmain.dto.compilation.NewCompilationDto;
import ru.practicum.ewmmain.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCompilationsController {

    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return compilationService.createCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable long compId) {
        compilationService.deleteCompilation(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(
            @PathVariable long compId,
            @PathVariable long eventId
    ) {
        compilationService.deleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void addEventToCompilation(
            @PathVariable long compId,
            @PathVariable long eventId
    ) {
        compilationService.addEventToCompilation(compId, eventId);
    }

    /**
     * Открепить подборку на главной странице
     * @param compId id подборки
     */
    @DeleteMapping("/{compId}/pin")
    public void unpinCompilation(@PathVariable long compId) {
        compilationService.unpinCompilation(compId);
    }

    /**
     * Закрепить подборку на главной странице
     * @param compId id подборки
     */
    @PatchMapping("/{compId}/pin")
    public void pinCompilation(@PathVariable long compId) {
        compilationService.pinCompilation(compId);
    }


}
