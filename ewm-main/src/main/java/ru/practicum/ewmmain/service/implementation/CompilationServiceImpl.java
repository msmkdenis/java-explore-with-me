package ru.practicum.ewmmain.service.implementation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.dto.compilation.CompilationDto;
import ru.practicum.ewmmain.dto.compilation.NewCompilationDto;
import ru.practicum.ewmmain.dto.mapper.CompilationMapper;
import ru.practicum.ewmmain.entity.Compilation;
import ru.practicum.ewmmain.entity.Event;
import ru.practicum.ewmmain.exception.EntityNotFoundException;
import ru.practicum.ewmmain.exception.ForbiddenError;
import ru.practicum.ewmmain.repository.CompilationRepository;
import ru.practicum.ewmmain.repository.EventRepository;
import ru.practicum.ewmmain.service.CompilationService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        Page<Compilation> compilations;
        if (isNull(pinned)) {
            compilations = compilationRepository.findAll(PageRequest.of(getPageNumber(from, size), size));
        } else {
            compilations = compilationRepository.findAllByPinned(
                    pinned,
                    PageRequest.of(getPageNumber(from, size), size)
            );
        }
        return compilations
                .stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(long id) {
        Compilation compilation = checkCompilation(id);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public CompilationDto createCompilation(@NonNull NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        Set<Event> events = eventRepository.getEventsByIds(newCompilationDto.getEvents());
        compilation.setEvents(events);
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilation(long compId) {
        Compilation compilation = checkCompilation(compId);
        compilationRepository.delete(compilation);
    }

    @Transactional
    @Override
    public void deleteEventFromCompilation(long compId, long eventId) {
        Compilation compilation = checkCompilation(compId);
        Event event = checkEvent(eventId);
        compilation.getEvents().remove(event);
    }

    @Override
    @Transactional
    public void addEventToCompilation(long compId, long eventId) {
        Compilation compilation = checkCompilation(compId);
        Event event = checkEvent(eventId);
        compilation.getEvents().add(event);
    }

    @Override
    @Transactional
    public void unpinCompilation(long compId) {
        Compilation compilation = checkCompilation(compId);
        if (!compilation.isPinned()) {
            throw new ForbiddenError(String.format("Compilation id = '%s' already unpinned", compId));
        }
        compilation.setPinned(false);
    }

    @Override
    @Transactional
    public void pinCompilation(long compId) {
        Compilation compilation = checkCompilation(compId);
        if (compilation.isPinned()) {
            throw new ForbiddenError(String.format("Compilation id = '%s' already pinned", compId));
        }
        compilation.setPinned(true);
    }

    private Compilation checkCompilation(Long id) {
        return compilationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Compilation id=%d не найден!", id)));
    }

    private Event checkEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Event id=%d не найден!", id)));
    }

    private int getPageNumber(int from, int size) {
        return from / size;
    }
}
