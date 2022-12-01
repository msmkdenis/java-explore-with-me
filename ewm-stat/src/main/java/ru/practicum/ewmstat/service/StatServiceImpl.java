package ru.practicum.ewmstat.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmstat.dto.NewEndPointHitDto;
import ru.practicum.ewmstat.dto.ViewStatsDto;
import ru.practicum.ewmstat.entity.App;
import ru.practicum.ewmstat.entity.EndPointHit;
import ru.practicum.ewmstat.entity.ViewStats;
import ru.practicum.ewmstat.exception.EntityNotFoundException;
import ru.practicum.ewmstat.mapper.EndPointHitMapper;
import ru.practicum.ewmstat.mapper.ViewStatsMapper;
import ru.practicum.ewmstat.repository.AppRepository;
import ru.practicum.ewmstat.repository.StatRepository;
import ru.practicum.ewmstat.specification.StatRequestParameters;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;
    private final AppRepository appRepository;

    @Override
    public void saveHit(@NonNull NewEndPointHitDto hit) {
        App app = appRepository.save(App.builder().name(hit.getApp()).build());
        statRepository.save(EndPointHitMapper.toEndPointHit(hit, app.getId()));
    }

    @Override
    public List<ViewStatsDto> getStats(@NonNull StatRequestParameters params) {
        List<EndPointHit> hits = statRepository.findAll(params.toSpecification());
        List<ViewStats> viewStats = hits.stream()
                .filter(distinctByKey(EndPointHit::getUri))
                .map(this::mapHitToView)
                .collect(Collectors.toList());

        if (params.isUnique()) {
            return viewStats.stream()
                    .peek(vs -> vs.setHits(statRepository.countDistinctByUri(vs.getUri(), params.getStart(), params.getEnd())))
                    .map(ViewStatsMapper::toViewStatsDto)
                    .collect(Collectors.toList());
        } else {
            return viewStats.stream()
                    .peek(vs -> vs.setHits(statRepository.countByUri(vs.getUri(), params.getStart(), params.getEnd())))
                    .map(ViewStatsMapper::toViewStatsDto)
                    .collect(Collectors.toList());
        }
    }

    private ViewStats mapHitToView(@NonNull EndPointHit hit) {
        return ViewStats.builder()
                .app(findAppOrThrow(hit.getAppId()).getName())
                .uri(hit.getUri())
                .build();
    }

    private App findAppOrThrow(Long appId) {
        return appRepository.findById(appId).orElseThrow(
                () -> new EntityNotFoundException(String.format("App id=%d не найден!", appId)));
    }

    private static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();

        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
