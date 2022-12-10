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
public class CommentFullDto {

    private long id;

    private String text;

    private long authorId;

    private long eventId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.datePattern)
    private LocalDateTime created;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.datePattern)
    private LocalDateTime moderated;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.datePattern)
    private LocalDateTime edited;

    private CommentStatus status;

    private int score;
}

