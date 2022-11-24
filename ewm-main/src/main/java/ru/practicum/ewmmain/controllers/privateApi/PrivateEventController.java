package ru.practicum.ewmmain.controllers.privateApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.dto.event.EventFullDto;
import ru.practicum.ewmmain.dto.event.EventShortDto;
import ru.practicum.ewmmain.dto.event.NewEventDto;
import ru.practicum.ewmmain.dto.event.UpdateEventRequest;
import ru.practicum.ewmmain.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewmmain.service.EventService;
import ru.practicum.ewmmain.service.ParticipationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final EventService eventService;
    private final ParticipationService participationService;

    @GetMapping
    public List<EventShortDto> getEventsByCurrentUser(
            @Positive @PathVariable("userId") long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size
    ) {
        log.info("PrivateEventController GET getEventsByCurrentUser userId: {}", userId);
        return eventService.getEventsByCurrentUser(userId, from, size);
    }

    @PatchMapping
    public EventFullDto updateEvent(
            @PathVariable long userId,
            @RequestBody @Valid UpdateEventRequest updateEventRequest
    ) {
        return eventService.updateEventByUser(userId, updateEventRequest);
    }

    @PostMapping
    public EventFullDto addEvent(
            @Positive @PathVariable("userId") long userId,
            @RequestBody @Valid NewEventDto newEventDto
    ) {
        log.info("PrivateEventController POST addEvent получен newEventDto: {}", newEventDto);
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEventById(
            @PathVariable long userId,
            @PathVariable long eventId
    ) {
        log.info("PrivateEventController GET getUserEventById userId: {} eventId: {}", userId, eventId);
        return eventService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto cancelEvent(
            @PathVariable long userId,
            @PathVariable long eventId
    ) {
        log.info("PrivateEventController PATCH cancelEvent userId: {} eventId: {}", userId, eventId);
        return eventService.cancelEvent(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventParticipationByOwner(
            @PathVariable long userId,
            @PathVariable long eventId
    ) {
        log.info("PrivateEventController GET getEventParticipationByOwner userId: {} eventId: {}", userId, eventId);
        return participationService.getEventParticipationByInitiator(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto approveEventRequest(
            @Positive @PathVariable("userId") Long userId,
            @Positive @PathVariable("eventId") Long eventId,
            @Positive @PathVariable("reqId") Long reqId
    ) {
        log.info("PrivateEventController PATCH approveEventRequest id = {}, user id = {}", reqId, userId);
        return participationService.approveEventRequest(userId, eventId, reqId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectEventRequest(
            @Positive @PathVariable("userId") Long userId,
            @Positive @PathVariable("eventId") Long eventId,
            @Positive @PathVariable("reqId") Long reqId
    ) {
        log.info("PrivateEventController PATCH rejectEventRequest id = {}, user id = {}", reqId, userId);
        return participationService.rejectEventRequest(userId, eventId, reqId);
    }

}
