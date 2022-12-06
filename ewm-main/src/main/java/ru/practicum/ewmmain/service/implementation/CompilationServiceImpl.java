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
import ru.practicum.ewmmain.entity.RequestQuantity;
import ru.practicum.ewmmain.exception.EntityNotFoundException;
import ru.practicum.ewmmain.exception.ForbiddenError;
import ru.practicum.ewmmain.repository.CompilationRepository;
import ru.practicum.ewmmain.repository.EventRepository;
import ru.practicum.ewmmain.repository.ParticipationRepository;
import ru.practicum.ewmmain.service.CompilationService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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

        Set<Event> events = compilations.stream().flatMap(compilation -> compilation.getEvents().stream())
                .collect(Collectors.toSet());
        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Object, Integer> views = statService.getStatisticsByEvents(events);

        return compilations
                .stream()
                .map(compilation -> CompilationMapper.toCompilationDto(
                        compilation,
                        getEventShortDto(compilation, confirmedRequests, views)))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getById(long id) {
        Compilation compilation = findCompilationOrThrow(id);
        Set<Event> events = compilation.getEvents();

        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Object, Integer> views = statService.getStatisticsByEvents(events);

        return CompilationMapper.toCompilationDto(compilation,
                getEventShortDto(compilation, confirmedRequests, views));
    }

    @Override
    public CompilationDto add(@NonNull NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        Set<Event> events = eventRepository.getEventsByIds(newCompilationDto.getEvents());
        compilation.setEvents(events);

        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Object, Integer> views = statService.getStatisticsByEvents(events);

        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation),
                getEventShortDto(compilation, confirmedRequests, views));
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

    private Set<EventShortDto> getEventShortDto(Compilation compilation,
                                                Map<Long, Long> confirmedRequests,
                                                Map<Object, Integer> views
    ) {

        Set<Event> events = compilation.getEvents();

        return events.stream()
                .map(event -> EventMapper.toEventShortDto(
                        event,
                        Math.toIntExact(confirmedRequests.getOrDefault(event.getId(),0L)),
                        views.getOrDefault((event.getId()), 0)))
                .collect(Collectors.toSet());
    }

    private Map<Long, Long> getConfirmedRequests(Collection<Event> events) {
        List<RequestQuantity> requestQuantities = participationRepository.countRequestsForEvents(events);

        return requestQuantities.stream()
                .collect(Collectors
                        .toMap(RequestQuantity::getRequestId, RequestQuantity::getRequestQuantity));
    }
}
