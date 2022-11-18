package ru.practicum.ewmmain.service;

import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.dto.compilation.CompilationDto;
import ru.practicum.ewmmain.dto.compilation.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(long id);

    CompilationDto createCompilation(@NonNull NewCompilationDto newCompilationDto);

    void deleteCompilation(long compId);

    @Transactional
    void deleteEventFromCompilation(long compId, long eventId);

    @Transactional
    void addEventToCompilation(long compId, long eventId);

    @Transactional
    void unpinCompilation(long compId);

    @Transactional
    void pinCompilation(long compId);
}
