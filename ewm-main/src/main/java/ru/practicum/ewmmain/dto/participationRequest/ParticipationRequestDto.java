package ru.practicum.ewmmain.dto.participationRequest;

import lombok.*;
import ru.practicum.ewmmain.entity.RequestStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;
    private Long event;
    private String created;
    private Long requester;
    private RequestStatus status;
}