package ru.practicum.ewmmain.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.dto.participationRequest.ParticipationRequestDto;

import java.util.List;

public interface ParticipationService {
    List<ParticipationRequestDto> getByInitiator(Long userId, Long eventId);

    ParticipationRequestDto approve(Long userId, Long eventId, Long reqId);

    ParticipationRequestDto reject(Long userId, Long eventId, Long reqId);

    List<ParticipationRequestDto> getByCurrentUser(Long userId);

    ParticipationRequestDto addByCurrentUser(long userId, long eventId);

    @Transactional
    ParticipationRequestDto cancel(long userId, long requestId);
}
