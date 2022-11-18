package ru.practicum.ewmmain.service;

import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.dto.event.*;
import ru.practicum.ewmmain.specification.adminEvents.AdminEventsRequestParameters;
import ru.practicum.ewmmain.specification.publicEvents.PublicEventsRequestParameters;

import java.util.List;

public interface EventService {
    EventFullDto addEvent(long userId, NewEventDto newEventDto);

    EventFullDto getEventById(long id);

    List<EventShortDto> getAllEvents(PublicEventsRequestParameters parameters);

    List<EventShortDto> getEventsByCurrentUser(long userId, int from, int size);

    @Transactional
    EventFullDto updateEventByUser(long userId, @NonNull UpdateEventRequest updateEventRequest);

    EventFullDto getUserEventById(long userId, long eventId);

    @Transactional
    EventFullDto cancelEvent(long userId, long eventId);

    List<EventFullDto> getAllEvents(AdminEventsRequestParameters parameters);

    @Transactional
    EventFullDto updateEventByAdmin(long eventId, @NonNull AdminUpdateEventRequest updateEventRequest);

    @Transactional
    EventFullDto publishEvent(long eventId);

    @Transactional
    EventFullDto rejectEvent(long eventId);
}
