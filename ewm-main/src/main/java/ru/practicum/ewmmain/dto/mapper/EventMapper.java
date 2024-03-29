package ru.practicum.ewmmain.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmmain.dto.comment.CommentFullDto;
import ru.practicum.ewmmain.dto.comment.CommentShortDto;
import ru.practicum.ewmmain.dto.event.EventFullDto;
import ru.practicum.ewmmain.dto.event.EventShortDto;
import ru.practicum.ewmmain.dto.event.NewEventDto;
import ru.practicum.ewmmain.entity.*;
import ru.practicum.ewmmain.util.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@UtilityClass
public class EventMapper {

    public static final String DATE_TIME_STRING = Constants.datePattern;
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
                .eventDate(newEventDto.getEventDate())
                .initiator(initiator)
                .location(location)
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .eventStatus(EventStatus.PENDING)
                .title(newEventDto.getTitle())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event,
                                              int requests,
                                              int views,
                                              List<CommentFullDto> comments,
                                              Double eventRating) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(requests)
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
                .views(views)
                .comments(comments)
                .eventRating(eventRating)
                .build();
    }

    public static EventShortDto toEventShortDto(Event event,
                                                int requests,
                                                int views,
                                                List<CommentShortDto> comments,
                                                Double eventRating) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(requests)
                .eventDate(event.getEventDate())
                .initiator(UserMapper.userShortDto(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(views)
                .comments(comments)
                .eventRating(eventRating)
                .build();
    }
}
