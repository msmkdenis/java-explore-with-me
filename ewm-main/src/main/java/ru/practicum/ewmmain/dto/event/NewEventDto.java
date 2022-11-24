package ru.practicum.ewmmain.dto.event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewmmain.dto.location.LocationDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class NewEventDto {
    @NotEmpty
    private String annotation;
    private Long category;
    @NotEmpty
    private String description;
    @NotEmpty
    private String eventDate;
    @NotNull
    private LocationDto location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    @NotEmpty
    private String title;

    public NewEventDto() {
        this.requestModeration = true;
    }
}
