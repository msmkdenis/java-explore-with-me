package ru.practicum.ewmmain.controllers.publicApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmmain.client.StatService;
import ru.practicum.ewmmain.dto.event.EventFullDto;
import ru.practicum.ewmmain.dto.event.EventShortDto;
import ru.practicum.ewmmain.service.EventService;
import ru.practicum.ewmmain.specification.publicEvents.PublicEventsRequestParameters;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;
    private final StatService statService;

    @GetMapping("/{id}")
    public EventFullDto getEventById(
            @PathVariable long id,
            HttpServletRequest request
    ) {
        log.info("PublicEventController GET getEvent id: {}", id);
        statService.postHit(request);
        return eventService.getEventById(id);
    }

    @GetMapping
    public List<EventShortDto> getAllEvents(
            PublicEventsRequestParameters parameters,
            HttpServletRequest request
    ) {
        log.info("PublicEventController GET getAllEvents parameters: {}", parameters);
        statService.postHit(request);
        return eventService.getAllEvents(parameters);
    }

/*    @GetMapping
    public List<EventShortDto> getAllEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") EventSortType sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size,
            HttpServletRequest request
    ) {
        PublicEventsRequestParameters parameters =
                PublicEventsRequestParameters.builder()
                        .text(text)
                        .categories(categories)
                        .paid(paid)
                        .rangeStart(rangeStart)
                        .rangeEnd(rangeEnd)
                        .onlyAvailable(onlyAvailable)
                                .
        log.info("PublicEventController GET getAllEvents parameters: {}", parameters);
        statService.postHit(request);
        return eventService.getAllEvents(parameters);
    }*/
}
