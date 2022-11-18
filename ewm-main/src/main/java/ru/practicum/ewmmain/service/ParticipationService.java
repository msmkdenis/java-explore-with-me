package ru.practicum.ewmmain.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.dto.participationRequest.ParticipationRequestDto;

import java.util.List;

public interface ParticipationService {
    List<ParticipationRequestDto> getEventParticipationByInitiator(Long userId, Long eventId);

    ParticipationRequestDto approveEventRequest(Long userId, Long eventId, Long reqId);

    ParticipationRequestDto rejectEventRequest(Long userId, Long eventId, Long reqId);

    List<ParticipationRequestDto> getRequestsByCurrentUser(Long userId);

    ParticipationRequestDto addRequestByCurrentUser(long userId, long eventId);

    @Transactional
    ParticipationRequestDto cancelRequest(long userId, long requestId);
}
