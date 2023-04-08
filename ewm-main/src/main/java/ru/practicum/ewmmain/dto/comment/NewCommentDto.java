package ru.practicum.ewmmain.dto.comment;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCommentDto {
    @NotBlank(message = "Отзыв не может быть пустым")
    @Size(max = 5000)
    private String text;
    @Positive(message = "Id события не может быть отрицательным")
    private Long eventId;
    @Min(value = 1, message = "Значение рейтинга должно быть не менее 1")
    @Max(value = 10, message = "Значение рейтинга должно быть не более 10")
    private int score;
}
