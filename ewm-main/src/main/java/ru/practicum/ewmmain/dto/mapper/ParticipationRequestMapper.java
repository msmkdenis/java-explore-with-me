package ru.practicum.ewmmain.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmmain.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewmmain.entity.ParticipationRequest;
import ru.practicum.ewmmain.entity.RequestStatus;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class ParticipationRequestMapper {

    public static final String DATE_TIME_STRING = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_STRING);

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .event(participationRequest.getEvent().getId())
                .created(participationRequest.getCreated().format(DATE_TIME_FORMATTER))
                .requester(participationRequest.getRequester().getId())
                .status(RequestStatus.valueOf(participationRequest.getRequestStatus().toString()))
                .build();
    }
}
