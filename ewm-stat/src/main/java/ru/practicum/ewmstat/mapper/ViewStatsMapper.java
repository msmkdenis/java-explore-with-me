package ru.practicum.ewmstat.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmstat.dto.ViewStatsDto;
import ru.practicum.ewmstat.entity.ViewStats;

@UtilityClass
public class ViewStatsMapper {

    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
    }
}
