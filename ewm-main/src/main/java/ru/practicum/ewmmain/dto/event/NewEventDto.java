package ru.practicum.ewmmain.dto.event;

import ru.practicum.ewmmain.dto.location.LocationDto;

public class NewEventDto {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private LocationDto location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    private String title;

    public NewEventDto() {
        this.requestModeration = true;
    }
}
