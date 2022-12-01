package ru.practicum.ewmmain.service.implementation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.client.StatService;
import ru.practicum.ewmmain.dto.compilation.CompilationDto;
import ru.practicum.ewmmain.dto.compilation.NewCompilationDto;
import ru.practicum.ewmmain.dto.event.EventShortDto;
import ru.practicum.ewmmain.dto.mapper.CompilationMapper;
import ru.practicum.ewmmain.dto.mapper.EventMapper;
import ru.practicum.ewmmain.entity.Compilation;
import ru.practicum.ewmmain.entity.Event;
import ru.practicum.ewmmain.entity.RequestStatus;
import ru.practicum.ewmmain.exception.EntityNotFoundException;
import ru.practicum.ewmmain.exception.ForbiddenError;
import ru.practicum.ewmmain.repository.CompilationRepository;
import ru.practicum.ewmmain.repository.EventRepository;
import ru.practicum.ewmmain.repository.ParticipationRepository;
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
    private final ParticipationRepository participationRepository;
    private final StatService statService;

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
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
                .map(compilation -> CompilationMapper.toCompilationDto(compilation, getEventShortDto(compilation)))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getById(long id) {
        Compilation compilation = findCompilationOrThrow(id);

        return CompilationMapper.toCompilationDto(compilation, getEventShortDto(compilation));
    }

    @Override
    public CompilationDto add(@NonNull NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        Set<Event> events = eventRepository.getEventsByIds(newCompilationDto.getEvents());
        compilation.setEvents(events);

        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation), getEventShortDto(compilation));
    }

    @Override
    public void delete(long compId) {
        Compilation compilation = findCompilationOrThrow(compId);
        compilationRepository.delete(compilation);
    }

    @Transactional
    @Override
    public void deleteEventFromCompilation(long compId, long eventId) {
        Compilation compilation = findCompilationOrThrow(compId);
        Event event = findEventOrThrow(eventId);
        compilation.getEvents().remove(event);
    }

    @Override
    @Transactional
    public void addEventToCompilation(long compId, long eventId) {
        Compilation compilation = findCompilationOrThrow(compId);
        Event event = findEventOrThrow(eventId);
        compilation.getEvents().add(event);
    }

    @Override
    @Transactional
    public void unpinCompilation(long compId) {
        Compilation compilation = findCompilationOrThrow(compId);
        if (!compilation.isPinned()) {
            throw new ForbiddenError(String.format("Compilation id = '%s' уже откреплена", compId));
        }
        compilation.setPinned(false);
    }

    @Override
    @Transactional
    public void pinCompilation(long compId) {
        Compilation compilation = findCompilationOrThrow(compId);
        if (compilation.isPinned()) {
            throw new ForbiddenError(String.format("Compilation id = '%s' уже закреплена", compId));
        }
        compilation.setPinned(true);
    }

    private Compilation findCompilationOrThrow(Long id) {
        return compilationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Compilation id=%d не найден!", id)));
    }

    private Event findEventOrThrow(Long id) {
        return eventRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Event id=%d не найден!", id)));
    }

    private int getPageNumber(int from, int size) {
        return from / size;
    }

    private int getConfirmedRequests(long eventId) {
        return participationRepository.countByEventIdAndRequestStatus(eventId, RequestStatus.CONFIRMED);
    }

    private Set<EventShortDto> getEventShortDto(Compilation compilation) {
        Set<Event> events = compilation.getEvents();
        return events.stream()
                .map(e -> EventMapper.toEventShortDto(e, getConfirmedRequests(e.getId()), getViews(e.getId())))
                .collect(Collectors.toSet());
    }

    private int getViews(long eventId) {
        return statService.getViews(eventId);
    }
}
