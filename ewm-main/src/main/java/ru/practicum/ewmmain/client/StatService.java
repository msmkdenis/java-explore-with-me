package ru.practicum.ewmmain.client;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewmmain.dto.client.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatService {

    private final RestTemplate restTemplate;

    @Value("${ewm-stat.url}")
    private String statUrl;

    public StatService() {
        this.restTemplate = new RestTemplate();
    }

    public void postHit(@NonNull HttpServletRequest request) {
        EndpointHitDto hitDto = new EndpointHitDto();
        hitDto.setApp("explore-with-me");
        hitDto.setUri(request.getRequestURI());
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setTimestamp(LocalDateTime.now());
        makeAndSendRequest(hitDto);
    }

    public int getViews(long eventId) {
        ResponseEntity<Object> responseEntity = getViewStats(LocalDateTime.of(2010, 12, 31, 0, 0),
                LocalDateTime.now(), List.of("/events/" + eventId), false);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            ArrayList<Object> body = (ArrayList<Object>) responseEntity.getBody();
            if (body.size() != 0) {
                Integer hits = 0;
                for (Object obj : body) {
                    Integer hit = (Integer) ((LinkedHashMap) obj).get("hits");
                    hits = hits + hit;
                }
                return hits;
            }
        }
        return 0;
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

    private ResponseEntity<Object> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String url = statUrl + "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        Map<String, Object> parameters = Map.of(
                "start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "uris", uris.get(0),
                "unique", unique
        );
        ViewStats viewStats = new ViewStats();
        HttpEntity<ViewStats> request = new HttpEntity<>(viewStats, defaultHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, request, Object.class, parameters);
    }
}
