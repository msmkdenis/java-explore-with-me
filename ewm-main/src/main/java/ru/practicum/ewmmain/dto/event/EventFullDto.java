package ru.practicum.ewmmain.dto.event;

import lombok.*;
import ru.practicum.ewmmain.dto.category.CategoryDto;
import ru.practicum.ewmmain.dto.user.UserShortDto;
import ru.practicum.ewmmain.entity.EventStatus;
import ru.practicum.ewmmain.entity.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFullDto {
    private Long id;
    @NotBlank
    @Size(max = 1000)
    private String annotation;
    @NotBlank
    private CategoryDto category;
    private int confirmedRequests;
    private String createdOn;
    @Size(max = 7000)
    private String description;
    @NotBlank
    private String eventDate;
    @NotBlank
    private UserShortDto initiator;
    @NotBlank
    private Location location;
    @NotBlank
    private boolean paid;
    private int participantLimit;
    private String publishedOn;
    private boolean requestModeration;
    @Size(max = 50)
    private EventStatus state;
    @Size(max = 100)
    private String title;
    private int views;
}
