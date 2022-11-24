package ru.practicum.ewmstat.service;

import lombok.NonNull;
import ru.practicum.ewmstat.entity.EndPointHit;
import ru.practicum.ewmstat.entity.ViewStats;
import ru.practicum.ewmstat.specification.StatRequestParameters;

import java.util.List;

public interface StatService {

    void saveHit(@NonNull EndPointHit hit);

    List<ViewStats> getStats(@NonNull StatRequestParameters params);
}
