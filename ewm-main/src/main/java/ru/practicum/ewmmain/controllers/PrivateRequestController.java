package ru.practicum.ewmmain.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.dto.event.EventShortDto;
import ru.practicum.ewmmain.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewmmain.service.ParticipationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {

    private final ParticipationService participationService;

    @GetMapping
    public List<ParticipationRequestDto> getRequestsByCurrentUser(
            @Positive @PathVariable("userId") Long userId
    ) {
        log.info("PrivateRequestController GET getRequestsByCurrentUser userId: {}", userId);
        return participationService.getRequestsByCurrentUser(userId);
    }

    @PostMapping
    public ParticipationRequestDto addRequestByCurrentUser(
            @Positive @PathVariable("userId") Long userId,
            @RequestParam long eventId
    ) {
        log.info("PrivateRequestController POST addRequestByCurrentUser userId: {}, eventId: {}", userId, eventId);
        return participationService.addRequestByCurrentUser(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(
            @PathVariable long userId,
            @PathVariable long requestId
    ) {
        log.info("PrivateRequestController PATCH cancelRequest userId: {}, requestId: {}", userId, requestId);
        return participationService.cancelRequest(userId, requestId);
    }

}
