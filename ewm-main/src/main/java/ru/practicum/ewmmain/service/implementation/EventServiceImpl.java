package ru.practicum.ewmmain.service.implementation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.dto.event.*;
import ru.practicum.ewmmain.dto.location.LocationDto;
import ru.practicum.ewmmain.dto.mapper.EventMapper;
import ru.practicum.ewmmain.dto.mapper.LocationMapper;
import ru.practicum.ewmmain.entity.*;
import ru.practicum.ewmmain.exception.EntityNotFoundException;
import ru.practicum.ewmmain.exception.ForbiddenError;
import ru.practicum.ewmmain.repository.*;
import ru.practicum.ewmmain.service.EventService;
import ru.practicum.ewmmain.specification.adminEvents.AdminEventsRequestParameters;
import ru.practicum.ewmmain.specification.publicEvents.PublicEventsRequestParameters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRepository participationRepository;

    @Transactional
    @Override
    public EventFullDto addEvent(long userId, NewEventDto newEventDto) {
        User initiator = checkUser(userId);
        Category category = checkCategory(newEventDto.getCategory());
        Location location = LocationMapper.toLocation(newEventDto.getLocation());
        Event event = EventMapper.toEvent(newEventDto, initiator, category, location);
        checkNewEventDate(event);
        locationRepository.save(location);
        eventRepository.save(event);

        return EventMapper.toEventFullDto(event, getConfirmedRequests(event.getId()));
    }

    @Override
    public EventFullDto getEventById(long id) {
        Event event = checkEvent(id);
        checkEventStatus(event);
        event.setViews(event.getViews() + 1);

        return EventMapper.toEventFullDto(event, getConfirmedRequests(event.getId()));
    }

/*    @Override
    public List<EventShortDto> getAllEvents(PublicEventsRequestParameters parameters) {
        return eventRepository.findAll(parameters.toSpecification(), parameters.toPageable())
                .stream()
                .map(event -> EventMapper.toEventShortDto(event, getConfirmedRequests(event.getId())))
                .collect(Collectors.toList());
    }*/

    @Override
    public List<EventShortDto> getAllEvents(PublicEventsRequestParameters parameters) {
        List<Event> events = eventRepository.findAll(parameters.toSpecification(), parameters.toPageable())
                .stream()
                .collect(Collectors.toList());
        if (parameters.getOnlyAvailable()) {
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() > getConfirmedRequests(event.getId()))
                    .collect(Collectors.toList());
        }
        return events.stream()
                .map(event -> EventMapper.toEventShortDto(event, getConfirmedRequests(event.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsByCurrentUser(long userId, int from, int size) {
        checkUser(userId);

        return eventRepository.findAllByInitiatorId(userId, PageRequest.of(getPageNumber(from, size), size)).stream()
                .map(event -> EventMapper.toEventShortDto(event, getConfirmedRequests(event.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(long userId, @NonNull UpdateEventRequest updateEventRequest) {
        Event event = checkEvent(updateEventRequest.getEventId());
        checkNewEventDate(event);
        updateEventFieldsByUser(event, updateEventRequest);

        return EventMapper.toEventFullDto(event, getConfirmedRequests(event.getId()));
    }

    @Override
    public EventFullDto getUserEventById(long userId, long eventId) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        checkEventInitiator(user, event);

        return EventMapper.toEventFullDto(event, getConfirmedRequests(event.getId()));
    }

    @Override
    @Transactional
    public EventFullDto cancelEvent(long userId, long eventId) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        checkEventInitiator(user, event);
        event.setEventStatus(EventStatus.CANCELED);

        return EventMapper.toEventFullDto(event, getConfirmedRequests(event.getId()));
    }

    @Override
    public List<EventFullDto> getAllEvents(AdminEventsRequestParameters parameters) {
        return eventRepository.findAll(parameters.toSpecification(), parameters.toPageable())
                .stream()
                .map(event -> EventMapper.toEventFullDto(event, getConfirmedRequests(event.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(long eventId, @NonNull AdminUpdateEventRequest updateEventRequest) {
        Event event = checkEvent(eventId);
        updateEventFieldsByAdmin(event, updateEventRequest);

        return EventMapper.toEventFullDto(event, getConfirmedRequests(event.getId()));
    }

    @Transactional
    @Override
    public EventFullDto publishEvent(long eventId) {
        Event event = checkEvent(eventId);
        checkEventForPublish(event);
        event.setEventStatus(EventStatus.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());

        return EventMapper.toEventFullDto(event, getConfirmedRequests(event.getId()));
    }

    @Transactional
    @Override
    public EventFullDto rejectEvent(long eventId) {
        Event event = checkEvent(eventId);
        checkEventForReject(event);
        event.setEventStatus(EventStatus.CANCELED);

        return EventMapper.toEventFullDto(event, getConfirmedRequests(event.getId()));
    }

    private User checkUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User id=%d не найден!", id)));
    }

    private Category checkCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Category id=%d не найден!", id)));
    }

    private Event checkEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Event id=%d не найден!", id)));
    }

    private Location checkLocation(Long id) {
        return locationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Location id=%d не найден!", id)));
    }

    private void checkEventStatus(Event event) {
        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new ForbiddenError(
                    String.format("Событие id=%d не опубликовано", event.getId())
            );
        }
    }

    private void checkNewEventDate(@NonNull Event event) {
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenError(
                    String.format("До события %s осталось меньше 2 часов!", event.getTitle())
            );
        }
    }

    private void checkEventInitiator(@NonNull User user, @NonNull Event event) {
        if (!Objects.equals(user.getId(), event.getInitiator().getId())) {
            throw new ForbiddenError(
                    String.format("User id=%d не является инициатором события id=%d", user.getId(), event.getId())
            );
        }
    }

    private void checkEventForPublish(Event event) {
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ForbiddenError(
                    String.format("До события id=%d (%s) осталось меньше 1 часа!", event.getId(), event.getTitle())
            );
        }
        if (!event.getEventStatus().equals(EventStatus.PENDING)) {
            throw new ForbiddenError(
                    String.format("Событие id=%d (%s) должно быть в состоянии ожидания публикации!", event.getId(), event.getTitle())
            );
        }
    }

    private void checkEventForReject(@NonNull Event event) {
        if (event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new ForbiddenError(
                    String.format("Событие id=%d (%s) уже опубликовано!", event.getId(), event.getTitle())
            );
        }
    }

    private int getPageNumber(int from, int size) {
        return from / size;
    }

    private void updateEventFieldsByUser(Event event, UpdateEventRequest updateEventRequest) {

        event.setEventStatus(EventStatus.PENDING);

        String annotation = updateEventRequest.getAnnotation();
        if (annotation != null) {
            event.setAnnotation(annotation);
        }

        Long categoryId = updateEventRequest.getCategory();
        if (categoryId != null) {
            Category newCategory = checkCategory(categoryId);
            event.setCategory(newCategory);
        }

        String description = updateEventRequest.getDescription();
        if (description != null) {
            event.setDescription(description);
        }

        String eventDate = updateEventRequest.getEventDate();
        if (eventDate != null) {
            event.setEventDate(LocalDateTime.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            checkNewEventDate(event);
        }

        Boolean paid = updateEventRequest.getPaid();
        if (paid != null) {
            event.setPaid(paid);
        }

        Integer participantLimit = updateEventRequest.getParticipantLimit();
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }

        String title = updateEventRequest.getTitle();
        if (title != null) {
            event.setTitle(title);
        }
    }

    private void updateEventFieldsByAdmin(Event event, AdminUpdateEventRequest updateEventRequest) {

        String annotation = updateEventRequest.getAnnotation();

        if (annotation != null) {
            event.setAnnotation(annotation);
        }

        Long categoryId = updateEventRequest.getCategory();
        if (categoryId != null) {
            Category newCategory = checkCategory(categoryId);
            event.setCategory(newCategory);
        }

        String description = updateEventRequest.getDescription();
        if (description != null) {
            event.setDescription(description);
        }

        String eventDate = updateEventRequest.getEventDate();
        if (eventDate != null) {
            event.setEventDate(LocalDateTime.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        LocationDto location = updateEventRequest.getLocation();
        if (location != null) {
            Location newLocation = checkLocation(event.getLocation().getId());
            newLocation.setLat(location.getLat());
            newLocation.setLon(location.getLon());
        }

        Boolean paid = updateEventRequest.getPaid();
        if (paid != null) {
            event.setPaid(paid);
        }

        Integer participantLimit = updateEventRequest.getParticipantLimit();
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }

        Boolean requestModeration = updateEventRequest.getRequestModeration();
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }

        String title = updateEventRequest.getTitle();
        if (title != null) {
            event.setTitle(title);
        }
    }

    private int getConfirmedRequests(long eventId) {
        return participationRepository.countByEventIdAndRequestStatus(eventId, RequestStatus.CONFIRMED);
    }
}
