package com.practicum.ewmmain.dto.event;

import com.practicum.ewmmain.dto.location.LocationDto;

public class AdminUpdateEventRequest {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;
}
