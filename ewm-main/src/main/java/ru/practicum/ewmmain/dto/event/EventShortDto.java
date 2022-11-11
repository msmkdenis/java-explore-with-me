package ru.practicum.ewmmain.dto.event;

import ru.practicum.ewmmain.category.CategoryDto;
import ru.practicum.ewmmain.user.UserShortDto;

import java.time.LocalDateTime;

public class EventShortDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private boolean paid;
    private String title;
    private int views;
}
