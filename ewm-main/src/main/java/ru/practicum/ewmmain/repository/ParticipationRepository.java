package ru.practicum.ewmmain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmmain.entity.Event;
import ru.practicum.ewmmain.entity.ParticipationRequest;
import ru.practicum.ewmmain.entity.RequestQuantity;
import ru.practicum.ewmmain.entity.RequestStatus;

import java.util.Collection;
import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByEventId(long eventId);

    @Query("select r.event.id as requestId, count (r.id) as requestQuantity " +
            "from ParticipationRequest as r group by r.event.id having r.event in :events")
    List<RequestQuantity> countRequestsForEvents(@Param("events") Collection<Event> events);

    int countByEventIdAndRequestStatus(long evenId, RequestStatus status);

    @Query("select count(r) from ParticipationRequest r where r.event.id = ?1 and r.requestStatus in ?2")
    long quantityEventRequests(Long id, Collection<RequestStatus> statuses);

    @Query("select r from ParticipationRequest r where r.event.id = ?1 and r.requestStatus = ?2")
    List<ParticipationRequest> getAllByEventIdAndStatus(Long id, RequestStatus status);

    @Query("select r from ParticipationRequest r where r.requester.id = ?1")
    List<ParticipationRequest> getAllByUserId(Long userId);

    @Query("select (count(r) > 0) from ParticipationRequest r where r.event.id = ?1 and r.requester.id = ?2")
    boolean findByEventAndRequester(Long eventId, Long requesterId);

    @Query("select r from ParticipationRequest r where r.id = ?1 and r.requester.id = ?2")
    ParticipationRequest findByIdAndAndRequesterId(Long requestId, Long requesterId);
}
