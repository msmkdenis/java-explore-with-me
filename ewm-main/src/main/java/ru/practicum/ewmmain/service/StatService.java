package ru.practicum.ewmmain.service;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewmmain.dto.client.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
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

    private void makeAndSendRequest(@NonNull EndpointHitDto body) {
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(body, defaultHeaders());
        restTemplate.exchange(statUrl, HttpMethod.POST, requestEntity, Object.class);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
