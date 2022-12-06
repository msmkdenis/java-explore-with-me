package ru.practicum.ewmmain.service;

import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.dto.event.*;
import ru.practicum.ewmmain.specification.adminEvents.AdminEventsRequestParameters;
import ru.practicum.ewmmain.specification.publicEvents.PublicEventsRequestParameters;

import java.util.List;

public interface EventService {
    EventFullDto add(long userId, NewEventDto newEventDto);

    EventFullDto getById(long id);

    List<EventShortDto> getAllByPublicUser(PublicEventsRequestParameters parameters);

    List<EventShortDto> getEventsByCurrentUser(long userId, int from, int size);

    @Transactional
    EventFullDto updateByUser(long userId, @NonNull UpdateEventRequest updateEventRequest);

    EventFullDto getUserEventById(long userId, long eventId);

    @Transactional
    EventFullDto cancel(long userId, long eventId);

    List<EventFullDto> getAllEventsByAdmin(AdminEventsRequestParameters parameters);

    @Transactional
    EventFullDto updateByAdmin(long eventId, @NonNull AdminUpdateEventRequest updateEventRequest);

    @Transactional
    EventFullDto publish(long eventId);

    @Transactional
    EventFullDto reject(long eventId);
}
