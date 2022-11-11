package ru.practicum.ewmmain.entity;

import ru.practicum.ewmmain.user.User;

import java.time.LocalDateTime;

public class ParticipationRequest {
    private Long id;
    private Event event;
    private User requester;
    private RequestStatus requestStatus;
    private LocalDateTime created;
}
