package ru.practicum.ewmmain.controllers.admin_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.dto.event.AdminUpdateEventRequest;
import ru.practicum.ewmmain.dto.event.EventFullDto;
import ru.practicum.ewmmain.service.EventService;
import ru.practicum.ewmmain.specification.admin_events.AdminEventsRequestParameters;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getAllEvents(AdminEventsRequestParameters parameters) {
        log.info("AdminEventController GET getAllEvents parameters: {}", parameters);
        return eventService.getAllEventsByAdmin(parameters);
    }

    @PutMapping("/{eventId}")
    public EventFullDto updateEvent(
            @PathVariable long eventId,
            @RequestBody AdminUpdateEventRequest updateEventRequest
    ) {
        log.info("AdminEventController PUT updateEventByAdmin eventId: {}, updateEventRequest: {}", eventId, updateEventRequest);
        return eventService.updateByAdmin(eventId, updateEventRequest);
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable long eventId) {
        log.info("AdminEventController PATCH publishEvent eventId: {}", eventId);
        return eventService.publish(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable long eventId) {
        log.info("AdminEventController PATCH rejectEvent eventId: {}", eventId);
        return eventService.reject(eventId);
    }
}
