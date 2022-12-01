package ru.practicum.ewmmain.service;

import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.dto.compilation.CompilationDto;
import ru.practicum.ewmmain.dto.compilation.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAll(Boolean pinned, int from, int size);

    CompilationDto getById(long id);

    CompilationDto add(@NonNull NewCompilationDto newCompilationDto);

    void delete(long compId);

    @Transactional
    void deleteEventFromCompilation(long compId, long eventId);

    @Transactional
    void addEventToCompilation(long compId, long eventId);

    @Transactional
    void unpinCompilation(long compId);

    @Transactional
    void pinCompilation(long compId);
}
