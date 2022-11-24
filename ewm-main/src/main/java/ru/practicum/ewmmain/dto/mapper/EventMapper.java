package ru.practicum.ewmmain.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmmain.dto.event.EventFullDto;
import ru.practicum.ewmmain.dto.event.EventShortDto;
import ru.practicum.ewmmain.dto.event.NewEventDto;
import ru.practicum.ewmmain.entity.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class EventMapper {

    public static final String DATE_TIME_STRING = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_STRING);

    public static Event toEvent(NewEventDto newEventDto,
                                User initiator,
                                Category category,
                                Location location) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(), DATE_TIME_FORMATTER))
                .initiator(initiator)
                .location(location)
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .eventStatus(EventStatus.PENDING)
                .title(newEventDto.getTitle())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(DATE_TIME_FORMATTER))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(DATE_TIME_FORMATTER))
                .initiator(UserMapper.userShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn() == null ? null : event.getPublishedOn().format(DATE_TIME_FORMATTER))
                .requestModeration(event.isRequestModeration())
                .state(event.getEventStatus())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate().format(DATE_TIME_FORMATTER))
                .initiator(UserMapper.userShortDto(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
}