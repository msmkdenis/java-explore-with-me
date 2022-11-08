package com.practicum.ewmmain.dto.compilation;

import com.practicum.ewmmain.dto.event.EventShortDto;

import java.util.List;

public class CompilationDto {
    private Long id;
    private String title;
    private boolean pinned;
    private List<EventShortDto> events;
}
