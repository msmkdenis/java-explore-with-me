package ru.practicum.ewmmain.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmmain.dto.compilation.CompilationDto;
import ru.practicum.ewmmain.dto.compilation.NewCompilationDto;
import ru.practicum.ewmmain.dto.event.EventShortDto;
import ru.practicum.ewmmain.entity.Compilation;

import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.isPinned())
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation, Set<EventShortDto> eventShortDto) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(eventShortDto)
                .build();
    }
}
