package com.practicum.ewmmain.dto.participationRequest;

import com.practicum.ewmmain.entity.RequestStatus;

import java.time.LocalDateTime;

public class ParticipationRequestDto {
    private Long id;
    private Long event;
    private LocalDateTime created;
    private Long requester;
    private RequestStatus status;
}
