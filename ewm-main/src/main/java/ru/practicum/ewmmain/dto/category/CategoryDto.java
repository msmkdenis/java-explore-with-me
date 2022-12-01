package ru.practicum.ewmmain.dto.category;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    @NotNull
    private Long id;
    @NotBlank
    @Size(max = 50)
    private String name;
}
