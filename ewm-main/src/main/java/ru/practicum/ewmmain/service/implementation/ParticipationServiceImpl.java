package ru.practicum.ewmmain.service.implementation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.dto.mapper.ParticipationRequestMapper;
import ru.practicum.ewmmain.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewmmain.entity.*;
import ru.practicum.ewmmain.exception.EntityNotFoundException;
import ru.practicum.ewmmain.exception.ForbiddenError;
import ru.practicum.ewmmain.repository.EventRepository;
import ru.practicum.ewmmain.repository.ParticipationRepository;
import ru.practicum.ewmmain.repository.UserRepository;
import ru.practicum.ewmmain.service.ParticipationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipationServiceImpl implements ParticipationService {

    private final ParticipationRepository participationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getByInitiator(Long userId, Long eventId) {
        User user = findUserOrThrow(userId);
        Event event = findEventOrThrow(eventId);
        checkEventInitiator(user, event);

        return participationRepository.findAllByEventId(eventId).stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto approve(Long userId, Long eventId, Long reqId) {
        Event event = findEventOrThrow(eventId);
        User user = findUserOrThrow(userId);
        checkEventInitiator(user, event);

        ParticipationRequest participationRequest = findParticipationRequestOrThrow(reqId);

        if (!participationRequest.getRequestStatus().equals(RequestStatus.PENDING)) {
            throw new ForbiddenError("Требуется статус PENDING");
        }

        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            participationRequest.setRequestStatus(RequestStatus.CONFIRMED);
            return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
        }

        checkRequestLimit(event);
        participationRequest.setRequestStatus(RequestStatus.CONFIRMED);
        long quantity = participationRepository.quantityEventRequests(event.getId(), List.of(RequestStatus.CONFIRMED));

        if (quantity == event.getParticipantLimit()) {
            participationRepository.getAllByEventIdAndStatus(eventId, RequestStatus.PENDING)
                    .forEach(r -> r.setRequestStatus(RequestStatus.CANCELED));
        }

        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }

    @Transactional
    @Override
    public ParticipationRequestDto reject(Long userId, Long eventId, Long reqId) {
        Event event = findEventOrThrow(eventId);
        User user = findUserOrThrow(userId);
        checkEventInitiator(user, event);
        ParticipationRequest participationRequest = findParticipationRequestOrThrow(reqId);
        participationRequest.setRequestStatus(RequestStatus.REJECTED);

        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }

    @Override
    public List<ParticipationRequestDto> getByCurrentUser(Long userId) {
        findUserOrThrow(userId);

        return participationRepository.getAllByUserId(userId).stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto addByCurrentUser(long userId, long eventId) {
        Event event = findEventOrThrow(eventId);
        User user = findUserOrThrow(userId);
        checkParticipationRequest(event, user);
        ParticipationRequest request = makeNewRequest(user, event);

        return ParticipationRequestMapper.toParticipationRequestDto(participationRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(long userId, long requestId) {
        ParticipationRequest request = findParticipationRequestOrThrow(requestId);
        findUserOrThrow(userId);
        request.setRequestStatus(RequestStatus.CANCELED);

        return ParticipationRequestMapper.toParticipationRequestDto(request);
    }

    private void checkEventInitiator(@NonNull User user, @NonNull Event event) {
        if (!Objects.equals(user.getId(), event.getInitiator().getId())) {
            throw new ForbiddenError(
                    String.format("User id=%d не является инициатором события id=%d", user.getId(), event.getId())
            );
        }
    }

    private Event findEventOrThrow(Long id) {
        return eventRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Event id=%d не найден!", id)));
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User id=%d не найден!", id)));
    }

    private ParticipationRequest findParticipationRequestOrThrow(Long id) {
        return participationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("ParticipationRequest id=%d не найден!", id)));
    }

    private void checkParticipationRequest(Event event, User user) {

        if (participationRepository.findByEventAndRequester(event.getId(), user.getId())) {
            throw new ForbiddenError(String.format("ParticipationRequest user id=%d на участие в событии id=%d уже есть",
                    user.getId(), event.getId()));
        }

        if (event.getInitiator().equals(user)) {
            throw new ForbiddenError(String.format("Пользователь id=%d является инициатором события id=%d",
                    user.getId(), event.getId()));
        }

        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new ForbiddenError(String.format("Событие id=%d не опубликовано", event.getId()));
        }

        if (participationRepository.quantityEventRequests(event.getId(), List.of(RequestStatus.PENDING,
                RequestStatus.CONFIRMED)) == event.getParticipantLimit()) {
            throw new ForbiddenError(
                    String.format("У события id=%d достигнут максимальный лимит запросов на участние", event.getId()));
        }
    }

    private void checkRequestLimit(Event event) {
        long quantity = participationRepository.quantityEventRequests(event.getId(), List.of(RequestStatus.CONFIRMED));
        if (quantity == event.getParticipantLimit()) {
            throw new ForbiddenError(
                    String.format("У события id=%d достигнут максимальный лимит участников", event.getId()));
        }
    }

    private ParticipationRequest makeNewRequest(User user, Event event) {
        ParticipationRequest request = new ParticipationRequest();
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        request.setRequester(user);
        if (event.isRequestModeration()) {
            request.setRequestStatus(RequestStatus.PENDING);
        } else {
            request.setRequestStatus(RequestStatus.CONFIRMED);
        }

        return request;
    }
}
