package ru.practicum.ewmmain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmmain.entity.ParticipationRequest;
import ru.practicum.ewmmain.entity.RequestStatus;

import java.util.Collection;
import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByEventId(long eventId);

    @Query("select count(r) from participation_requests r where r.event.id = ?1 and r.requestStatus in ?2")
    long quantityEventRequests(Long id, Collection<RequestStatus> statuses);

    @Query("select r from participation_requests r where r.event.id = ?1 and r.requestStatus = ?2")
    List<ParticipationRequest> getAllByEventIdAndStatus(Long id, RequestStatus status);

    @Query("select r from participation_requests r where r.requester.id = ?1")
    List<ParticipationRequest> getAllByUserId(Long userId);

    @Query("select (count(r) > 0) from participation_requests r where r.event.id = ?1 and r.requester.id = ?2")
    boolean findByEventAndRequester(Long eventId, Long requesterId);
}
