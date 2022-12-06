package ru.practicum.ewmmain.client;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewmmain.dto.client.EndpointHitDto;
import ru.practicum.ewmmain.entity.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatService {

    private final RestTemplate restTemplate;
    private final String statUrl;
    private final String appName;

    private static final String GET_STAT_ENDPOINT = "/stats?start={rangeStart}&end={rangeEnd}&uris={uris}";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String EVENT_BASE_URI = "/events/";
    private static final long SHIFT_100_YEARS = 100;

    public StatService(
            RestTemplate restTemplate,
            @Value("${ewm-stat.url}") String statUrl,
            @Value("${whole-app.name}") String appName) {
        this.restTemplate = restTemplate;
        this.statUrl = statUrl;
        this.appName = appName;
    }

    public void postHit(@NonNull HttpServletRequest request) {
        EndpointHitDto hitDto = new EndpointHitDto();
        hitDto.setApp(appName);
        hitDto.setUri(request.getRequestURI());
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setTimestamp(LocalDateTime.now());
        makeAndSendRequest(hitDto);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return headers;
    }

    private void makeAndSendRequest(@NonNull EndpointHitDto body) {
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(body, defaultHeaders());
        restTemplate.exchange(statUrl + "/hit", HttpMethod.POST, requestEntity, Object.class);
    }

    public int getStatisticsByEvent(@NonNull Event event) {
        List<ViewStats> stat = getStatistics(List.of(EVENT_BASE_URI + event.getId()),
                event.getCreatedOn(), LocalDateTime.now().plusYears(SHIFT_100_YEARS));

        if (stat.isEmpty()) {
            return 0;
        }

        if (stat.size() > 1) {
            log.warn("getStatisticsByEvent: сервер статистики вернул данные более чем для 1 url");
            return 0;
        }
        return stat.get(0).getHits();
    }

    public Map<Object, Integer> getStatisticsByEvents(Collection<Event> events) {

        List<String> eventsUris = events.stream()
                .map(event -> EVENT_BASE_URI + event.getId())
                .collect(Collectors.toList());

        if (eventsUris.isEmpty()) {
            return Map.of();
        }

        List<ViewStats> eventsStat = getStatistics(eventsUris, LocalDateTime.now()
                .minusYears(SHIFT_100_YEARS), LocalDateTime.now().plusYears(SHIFT_100_YEARS));

        return eventsStat.stream().collect(
                Collectors.toMap(
                        view -> {
                            String eventIdString = view.getUri().substring(EVENT_BASE_URI.length());
                            return Long.parseLong(eventIdString);
                        },
                        ViewStats::getHits));
    }

    private List<ViewStats> getStatistics(@NonNull List<String> uris,
                                          @NonNull LocalDateTime rangeStart,
                                          @NonNull LocalDateTime rangeEnd
    ) {
        String urisString = String.join(",", uris);
        HttpEntity<ViewStats> httpEntity = new HttpEntity<>(defaultHeaders());

        ResponseEntity<List<ViewStats>> response = restTemplate.exchange(
                statUrl + GET_STAT_ENDPOINT,
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {},
                Map.of("rangeStart", rangeStart.format(DATE_TIME_FORMATTER),
                        "rangeEnd", rangeEnd.format(DATE_TIME_FORMATTER),
                        "uris", urisString));

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn("При получении статистики сервер вернул ошибку {}", response.getStatusCode());
            return new ArrayList<>();
        }

        log.info("Запуск метода обращения в сервис статистики");
        return response.getBody().stream().filter(view -> view.getApp().equals(appName)).collect(Collectors.toList());
    }
}
