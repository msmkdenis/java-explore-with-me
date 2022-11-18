package ru.practicum.ewmmain.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmmain.dto.category.CategoryDto;
import ru.practicum.ewmmain.dto.event.EventFullDto;
import ru.practicum.ewmmain.dto.event.EventShortDto;
import ru.practicum.ewmmain.service.EventService;
import ru.practicum.ewmmain.specification.publicEvents.PublicEventsRequestParameters;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable long id)
    {
        log.info("PublicEventController GET getEvent id: {}", id);
        return eventService.getEventById(id);
    }

    @GetMapping
    public List<EventShortDto> getAllEvents(
            PublicEventsRequestParameters parameters
    ) {
        log.info("PublicEventController GET getAllEvents parameters: {}", parameters);
        return eventService.getAllEvents(parameters);
    }

}
