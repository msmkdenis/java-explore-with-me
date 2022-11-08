package com.practicum.ewmmain.dto.event;

import com.practicum.ewmmain.dto.category.CategoryDto;
import com.practicum.ewmmain.dto.user.UserShortDto;
import com.practicum.ewmmain.entity.EventStatus;
import com.practicum.ewmmain.entity.Location;

import java.time.LocalDateTime;

public class EventFullDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    private LocalDateTime createdOn;
    private String description;
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Location location;
    private boolean paid;
    private int participantLimit;
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private EventStatus eventStatus;
    private String title;
    private int views;
}
