package ru.practicum.ewmmain.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewmmain.dto.category.CategoryDto;
import ru.practicum.ewmmain.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventShortDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private boolean paid;
    private String title;
    private int views;
}
