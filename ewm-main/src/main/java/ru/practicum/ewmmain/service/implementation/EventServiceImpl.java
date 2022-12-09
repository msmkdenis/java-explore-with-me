package ru.practicum.ewmmain.service.implementation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.client.StatService;
import ru.practicum.ewmmain.dto.comment.CommentFullDto;
import ru.practicum.ewmmain.dto.comment.CommentShortDto;
import ru.practicum.ewmmain.dto.event.*;
import ru.practicum.ewmmain.dto.location.LocationDto;
import ru.practicum.ewmmain.dto.mapper.CommentMapper;
import ru.practicum.ewmmain.dto.mapper.EventMapper;
import ru.practicum.ewmmain.dto.mapper.LocationMapper;
import ru.practicum.ewmmain.entity.*;
import ru.practicum.ewmmain.exception.EntityNotFoundException;
import ru.practicum.ewmmain.exception.ForbiddenError;
import ru.practicum.ewmmain.repository.*;
import ru.practicum.ewmmain.service.EventService;
import ru.practicum.ewmmain.specification.admin_events.AdminEventsRequestParameters;
import ru.practicum.ewmmain.specification.public_events.PublicEventsRequestParameters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final StatService statService;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public EventFullDto add(long userId, NewEventDto newEventDto) {
        checkNewEventDate(newEventDto.getEventDate());
        User initiator = findUserOrThrow(userId);
        Category category = findCategoryOrThrow(newEventDto.getCategory());
        Location location = LocationMapper.toLocation(newEventDto.getLocation());

        if (findByLatAndLon(location.getLat(), location.getLon()).isPresent()) {
            location = findByLatAndLon(location.getLat(), location.getLon()).get();
            Event event = EventMapper.toEvent(newEventDto, initiator, category, location);
            eventRepository.save(event);

            return getEventFullDto(event);
        } else {
            locationRepository.save(location);
            Event event = EventMapper.toEvent(newEventDto, initiator, category, location);
            eventRepository.save(event);

            return getEventFullDto(event);
        }
    }

    @Override
    public EventFullDto getById(long id) {
        Event event = findEventOrThrow(id);
        checkEventStatus(event);

        return getEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getAllByPublicUser(PublicEventsRequestParameters parameters) {
        List<Event> events = eventRepository.findAll(parameters.toSpecification(), parameters.toPageable())
                .stream()
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequests = getConfirmedRequestsByEvents(events);
        Map<Object, Integer> views = statService.getStatisticsByEvents(events);

        if (parameters.getOnlyAvailable()) {
            events = events.stream()
                    .filter(e -> e.getParticipantLimit() > Math.toIntExact(confirmedRequests.getOrDefault(e.getId(), 0L)))
                    .collect(Collectors.toList());
        }

        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<Comment> comments = commentRepository.findPublishedByEvents(CommentStatus.PUBLISHED, eventIds);
        List<CommentShortDto> commentShortDto = comments.stream()
                .map(CommentMapper::toCommentShortDto)
                .collect(Collectors.toList());

        Map<Long, Double> ratingToApi = getRatings();

        List<EventShortDto> eventShortDto = events.stream()
                .map(event -> EventMapper.toEventShortDto(
                        event,
                        Math.toIntExact(confirmedRequests.getOrDefault(event.getId(), 0L)),
                        views.getOrDefault((event.getId()), 0),
                        commentShortDto.stream().filter(c -> c.getEventId() == event.getId()).collect(Collectors.toList()),
                        ratingToApi.get(event.getId())))
                .collect(Collectors.toList());

        if (!(parameters.getLowestRating() == null)) {
            eventShortDto = eventShortDto.stream()
                    .filter(c -> (c.getEventRating() != null ? c.getEventRating() : 0) >= parameters.getLowestRating())
                    .collect(Collectors.toList());
        }

        if (!(parameters.getHighestRating() == null)) {
            eventShortDto = eventShortDto.stream()
                    .filter(c -> (c.getEventRating() != null ? c.getEventRating() : 0) <= parameters.getHighestRating())
                    .collect(Collectors.toList());
        }

        if (parameters.getSortByRating() == Boolean.TRUE) {
            eventShortDto = eventShortDto.stream()
                    .sorted(Comparator.comparing(EventShortDto::getEventRating, Comparator.nullsFirst(Comparator.naturalOrder())).reversed())
                    .collect(Collectors.toList());
        }

        return eventShortDto;
    }

    @Override
    public List<EventShortDto> getEventsByCurrentUser(long userId, int from, int size) {
        findUserOrThrow(userId);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, PageRequest.of(getPageNumber(from, size), size));

        Map<Long, Long> confirmedRequests = getConfirmedRequestsByEvents(events);
        Map<Object, Integer> views = statService.getStatisticsByEvents(events);

        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<Comment> comments = commentRepository.findPublishedByEvents(CommentStatus.PUBLISHED, eventIds);
        List<CommentShortDto> commentShortDto = comments.stream()
                .map(CommentMapper::toCommentShortDto)
                .collect(Collectors.toList());

        Map<Long, Double> ratingToApi = getRatings();

        return events.stream()
                .map(event -> EventMapper.toEventShortDto(
                        event,
                        Math.toIntExact(confirmedRequests.getOrDefault(event.getId(), 0L)),
                        views.getOrDefault((event.getId()), 0),
                        commentShortDto.stream().filter(c -> c.getEventId() == event.getId()).collect(Collectors.toList()),
                        ratingToApi.get(event.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateByUser(long userId, @NonNull UpdateEventRequest updateEventRequest) {
        Event event = findEventOrThrow(updateEventRequest.getEventId());
        checkNewEventDate(updateEventRequest.getEventDate());
        updateEventFieldsByUser(event, updateEventRequest);

        return getEventFullDto(event);
    }

    @Override
    public EventFullDto getUserEventById(long userId, long eventId) {
        User user = findUserOrThrow(userId);
        Event event = findEventOrThrow(eventId);
        checkEventInitiator(user, event);

        return getEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto cancel(long userId, long eventId) {
        User user = findUserOrThrow(userId);
        Event event = findEventOrThrow(eventId);
        checkEventInitiator(user, event);
        if (!event.getEventStatus().equals(EventStatus.PENDING)) {
            throw new ForbiddenError(
                    String.format("У события id=%d некорректный статус", event.getId()));
        }
        event.setEventStatus(EventStatus.CANCELED);

        return getEventFullDto(event);
    }

    @Override
    public List<EventFullDto> getAllEventsByAdmin(AdminEventsRequestParameters parameters) {
        List<Event> events = eventRepository.findAll(parameters.toSpecification(), parameters.toPageable())
                .stream().collect(Collectors.toList());

        Map<Long, Long> confirmedRequests = getConfirmedRequestsByEvents(events);
        Map<Object, Integer> views = statService.getStatisticsByEvents(events);

        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<Comment> comments = commentRepository.findAllByEvents(eventIds);
        List<CommentFullDto> commentFullDto = comments.stream()
                .map(CommentMapper::toCommentFullDto)
                .collect(Collectors.toList());

        Map<Long, Double> ratingToApi = getRatings();

        List<EventFullDto> eventFullDto = events.stream()
                .map(event -> EventMapper.toEventFullDto(
                        event,
                        Math.toIntExact(confirmedRequests.getOrDefault(event.getId(), 0L)),
                        views.getOrDefault((event.getId()), 0),
                        commentFullDto.stream().filter(c -> c.getEventId() == event.getId()).collect(Collectors.toList()),
                        ratingToApi.get(event.getId())))
                .collect(Collectors.toList());

        if (!(parameters.getLowestRating() == null)) {
            eventFullDto = eventFullDto.stream()
                    .filter(c -> (c.getEventRating() != null ? c.getEventRating() : 0) >= parameters.getLowestRating())
                    .collect(Collectors.toList());
        }

        if (!(parameters.getHighestRating() == null)) {
            eventFullDto = eventFullDto.stream()
                    .filter(c -> (c.getEventRating() != null ? c.getEventRating() : 0) <= parameters.getHighestRating())
                    .collect(Collectors.toList());
        }

        if (parameters.getSortByRating() == Boolean.TRUE) {
            eventFullDto = eventFullDto.stream()
                    .sorted(Comparator.comparing(EventFullDto::getEventRating, Comparator.nullsFirst(Comparator.naturalOrder())).reversed())
                    .collect(Collectors.toList());
        }

        return eventFullDto;
    }

    @Transactional
    @Override
    public EventFullDto updateByAdmin(long eventId, @NonNull AdminUpdateEventRequest updateEventRequest) {
        Event event = findEventOrThrow(eventId);
        updateEventFieldsByAdmin(event, updateEventRequest);

        return getEventFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto publish(long eventId) {
        Event event = findEventOrThrow(eventId);
        checkEventForPublish(event);
        event.setEventStatus(EventStatus.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());

        return getEventFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto reject(long eventId) {
        Event event = findEventOrThrow(eventId);
        checkEventForReject(event);
        event.setEventStatus(EventStatus.CANCELED);

        return getEventFullDto(event);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User id=%d не найден!", id)));
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Category id=%d не найден!", id)));
    }

    private Event findEventOrThrow(Long id) {
        return eventRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Event id=%d не найден!", id)));
    }

    private Location findLocationOrThrow(Long id) {
        return locationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Location id=%d не найден!", id)));
    }

    private void checkEventStatus(Event event) {
        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new ForbiddenError(
                    String.format("Событие id=%d не опубликовано", event.getId()));
        }
    }

    private void checkNewEventDate(@NonNull LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenError("До события осталось меньше 2 часов!");
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
        if (!annotation.isBlank()) {
            event.setAnnotation(annotation);
        }

        Long categoryId = updateEventRequest.getCategory();
        if (categoryId != null) {
            Category newCategory = findCategoryOrThrow(categoryId);
            event.setCategory(newCategory);
        }

        String description = updateEventRequest.getDescription();
        if (!description.isBlank()) {
            event.setDescription(description);
        }

        LocalDateTime eventDate = updateEventRequest.getEventDate();
        if (eventDate != null) {
            event.setEventDate(eventDate);
            checkNewEventDate(event.getEventDate());
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
        if (!title.isBlank()) {
            event.setTitle(title);
        }
    }

    private void updateEventFieldsByAdmin(Event event, AdminUpdateEventRequest updateEventRequest) {

        String annotation = updateEventRequest.getAnnotation();

        if (!annotation.isBlank()) {
            event.setAnnotation(annotation);
        }

        Long categoryId = updateEventRequest.getCategory();
        if (categoryId != null) {
            Category newCategory = findCategoryOrThrow(categoryId);
            event.setCategory(newCategory);
        }

        String description = updateEventRequest.getDescription();
        if (!description.isBlank()) {
            event.setDescription(description);
        }

        String eventDate = updateEventRequest.getEventDate();
        if (eventDate != null) {
            event.setEventDate(LocalDateTime.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        LocationDto location = updateEventRequest.getLocation();
        if (location != null) {
            Location newLocation = findLocationOrThrow((event.getLocation().getId()));
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
        if (!title.isBlank()) {
            event.setTitle(title);
        }
    }

    private EventFullDto getEventFullDto(Event event) {
        return EventMapper.toEventFullDto(
                event,
                getConfirmedRequestsByEventId(event.getId()),
                statService.getStatisticsByEvent(event),
                getComments(event),
                getRatingForEvent(event));
    }

    Double getRatingForEvent(Event event) {
        Map<Long, Double> ratings = getRatings();
        if (ratings.get(event.getId()) == null) {
            return null;
        } else {
            return ratings.get(event.getId());
        }
    }

    List<CommentFullDto> getComments(Event event) {
        if (!(event.getComments() == null)) {
            return event.getComments().stream().map(CommentMapper::toCommentFullDto).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    Map<Long, Double> getRatings() {
        List<EventRating> eventRating = commentRepository.countEventRating();
        Map<Long, Double> rating = eventRating.stream()
                .collect(Collectors.toMap(EventRating::getEventId, EventRating::getEventScore));
        Map<Long, Double> ratingToApi = new HashMap<>(Collections.emptyMap());
        for (Map.Entry<Long, Double> pair : rating.entrySet()) {
            ratingToApi.put(pair.getKey(), Precision.round(pair.getValue(), 2));
        }
        return ratingToApi;
    }


    private Map<Long, Long> getConfirmedRequestsByEvents(Collection<Event> events) {
        List<RequestQuantity> requestQuantities = participationRepository.countRequestsForEvents(events);

        return requestQuantities.stream()
                .collect(Collectors.toMap(RequestQuantity::getRequestId, RequestQuantity::getRequestQuantity));
    }

    private int getConfirmedRequestsByEventId(long eventId) {
        return participationRepository.countByEventIdAndRequestStatus(eventId, RequestStatus.CONFIRMED);
    }

    private Optional<Location> findByLatAndLon(Float lat, Float lon) {
        return locationRepository.findLocationByLatAndLon(lat, lon);
    }

}
