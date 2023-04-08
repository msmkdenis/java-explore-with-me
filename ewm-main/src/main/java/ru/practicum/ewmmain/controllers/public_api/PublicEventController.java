package ru.practicum.ewmmain.controllers.public_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.client.StatService;
import ru.practicum.ewmmain.dto.comment.CommentShortDto;
import ru.practicum.ewmmain.dto.event.EventFullDto;
import ru.practicum.ewmmain.dto.event.EventShortDto;
import ru.practicum.ewmmain.service.CommentService;
import ru.practicum.ewmmain.service.EventService;
import ru.practicum.ewmmain.specification.public_events.PublicEventsRequestParameters;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;
    private final StatService statService;
    private final CommentService commentService;

    @GetMapping("/{id}")
    public EventFullDto getEventById(
            @PathVariable long id,
            HttpServletRequest request
    ) {
        log.info("PublicEventController GET getEvent id: {}", id);
        statService.postHit(request);
        return eventService.getById(id);
    }

    @GetMapping("/{id}/comments")
    public List<CommentShortDto> getAllCommentByEvent(
            @Positive @PathVariable("id") long eventId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size
    ) {
        log.info("PrivateCommentController GET getAllCommentByEvent id: {}", eventId);
        return commentService.getAllByEvent(eventId, from, size);
    }

    @GetMapping
    public List<EventShortDto> getAllEvents(
            PublicEventsRequestParameters parameters,
            HttpServletRequest request
    ) {
        log.info("PublicEventController GET getAllEvents parameters: {}", parameters);
        statService.postHit(request);
        return eventService.getAllByPublicUser(parameters);
    }
}
