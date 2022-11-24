package ru.practicum.ewmmain.controllers.adminApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.dto.event.AdminUpdateEventRequest;
import ru.practicum.ewmmain.dto.event.EventFullDto;
import ru.practicum.ewmmain.service.EventService;
import ru.practicum.ewmmain.specification.adminEvents.AdminEventsRequestParameters;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getAllEvents(AdminEventsRequestParameters parameters) {
        log.info("AdminEventController GET getAllEvents parameters: {}", parameters);
        return eventService.getAllEvents(parameters);
    }

    @PutMapping("/{eventId}")
    public EventFullDto updateEvent(
            @PathVariable long eventId,
            @RequestBody AdminUpdateEventRequest updateEventRequest
    ) {
        return eventService.updateEventByAdmin(eventId, updateEventRequest);
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable long eventId) {
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable long eventId) {
        return eventService.rejectEvent(eventId);
    }
}
