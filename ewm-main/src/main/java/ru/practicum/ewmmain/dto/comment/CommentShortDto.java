package ru.practicum.ewmmain.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewmmain.entity.CommentStatus;
import ru.practicum.ewmmain.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentShortDto {

    private long id;

    private String text;

    private long authorId;

    private long eventId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.datePattern)
    private LocalDateTime created;

    private CommentStatus status;

    private int score;
}
