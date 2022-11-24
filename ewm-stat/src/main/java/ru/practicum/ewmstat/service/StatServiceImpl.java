package ru.practicum.ewmstat.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmstat.entity.EndPointHit;
import ru.practicum.ewmstat.entity.ViewStats;
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

    @Override
    public void saveHit(@NonNull EndPointHit hit) {
        statRepository.save(hit);
    }

    @Override
    public List<ViewStats> getStats(@NonNull StatRequestParameters params) {
        List<EndPointHit> hits = statRepository.findAll(params.toSpecification());
        List<ViewStats> viewStats = hits.stream()
                .filter(distinctByKey(EndPointHit::getUri))
                .map(this::mapHitToView)
                .collect(Collectors.toList());

        if (params.isUnique()) {
            return viewStats.stream()
                    .peek(vs -> vs.setHits(statRepository.countDistinctByUri(vs.getUri(), params.getStart(), params.getEnd())))
                    .collect(Collectors.toList());
        } else {
            return viewStats.stream()
                    .peek(vs -> vs.setHits(statRepository.countByUri(vs.getUri(), params.getStart(), params.getEnd())))
                    .collect(Collectors.toList());
        }
    }

    private ViewStats mapHitToView(@NonNull EndPointHit hit) {
        ViewStats viewStats = new ViewStats();
        viewStats.setApp(hit.getApp());
        viewStats.setUri(hit.getUri());
        return viewStats;
    }

    private static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}