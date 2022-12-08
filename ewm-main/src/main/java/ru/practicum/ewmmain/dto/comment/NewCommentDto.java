package ru.practicum.ewmmain.dto.comment;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCommentDto {
    private String text;
    private Long eventId;
    @Min(value = 1, message = "Значение рейтинга должно быть не менее 1")
    @Max(value = 10, message = "Значение рейтинга должно быть не более 10")
    private int score;
}
