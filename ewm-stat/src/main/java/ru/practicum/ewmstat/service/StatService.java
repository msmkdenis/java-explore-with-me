package ru.practicum.ewmstat.service;

import lombok.NonNull;
import ru.practicum.ewmstat.dto.NewEndPointHitDto;
import ru.practicum.ewmstat.dto.ViewStatsDto;
import ru.practicum.ewmstat.specification.StatRequestParameters;

import java.util.List;

public interface StatService {

    void saveHit(@NonNull NewEndPointHitDto hit);

    List<ViewStatsDto> getStats(@NonNull StatRequestParameters params);
}
