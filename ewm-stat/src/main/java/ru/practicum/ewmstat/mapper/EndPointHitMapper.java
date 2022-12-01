package ru.practicum.ewmstat.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmstat.dto.EndPointHitDto;
import ru.practicum.ewmstat.dto.NewEndPointHitDto;
import ru.practicum.ewmstat.entity.EndPointHit;

@UtilityClass
public class EndPointHitMapper {

    public static EndPointHit toEndPointHit(NewEndPointHitDto newEndPointHitDto, long appId) {
        return EndPointHit.builder()
                .appId(appId)
                .ip(newEndPointHitDto.getIp())
                .uri(newEndPointHitDto.getUri())
                .timestamp(newEndPointHitDto.getTimestamp())
                .build();
    }

    public static EndPointHitDto toEndPointHitDto(EndPointHit endPointHit, String appName) {
        return EndPointHitDto.builder()
                .id(endPointHit.getId())
                .app(appName)
                .ip(endPointHit.getIp())
                .uri(endPointHit.getUri())
                .timestamp(endPointHit.getTimestamp())
                .build();
    }
}
