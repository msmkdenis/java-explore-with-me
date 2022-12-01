package ru.practicum.ewmmain.dto.compilation;

import lombok.*;
import ru.practicum.ewmmain.dto.event.EventShortDto;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {
    private Long id;
    private String title;
    private boolean pinned;
    private Set<EventShortDto> events;
}
