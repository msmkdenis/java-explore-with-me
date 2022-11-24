package ru.practicum.ewmmain.dto.compilation;

import lombok.*;
import ru.practicum.ewmmain.dto.event.EventShortDto;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {
    @NotBlank
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    private boolean pinned;
    private Set<EventShortDto> events;
}
