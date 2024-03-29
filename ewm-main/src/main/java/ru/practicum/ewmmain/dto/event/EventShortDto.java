package ru.practicum.ewmmain.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewmmain.dto.category.CategoryDto;
import ru.practicum.ewmmain.dto.comment.CommentShortDto;
import ru.practicum.ewmmain.dto.user.UserShortDto;
import ru.practicum.ewmmain.util.Constants;

import java.time.LocalDateTime;
import java.util.List;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.datePattern)
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private boolean paid;
    private String title;
    private int views;
    private List<CommentShortDto> comments;
    private Double eventRating;
}
