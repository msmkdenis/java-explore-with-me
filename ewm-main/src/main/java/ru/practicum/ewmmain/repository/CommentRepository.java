package ru.practicum.ewmmain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmmain.entity.Comment;
import ru.practicum.ewmmain.entity.CommentStatus;
import ru.practicum.ewmmain.entity.EventRating;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

    Page<Comment> findAllByAuthorId(Long userId, Pageable pageRequest);

    @Query("select c from Comment c where c.event.id=?1 and c.status=?2")
    Page<Comment> findAllByEventId(Long eventId, CommentStatus status, Pageable pageRequest);

    @Query("select c from Comment c where c.event.id=?1")
    Page<Comment> findAllByEventIdAdmin(Long eventId, Pageable pageRequest);

    Optional<Comment> findCommentByIdAndAuthorId(Long commentId, Long authorId);

    @Query(value = "select event_id as eventId, AVG(score) as eventScore from " +
            "(select event_id, score from comments where status like 'PUBLISHED') comments group by event_id", nativeQuery = true)
    List<EventRating> countEventRating();

    @Query("select c from Comment c where c.event.id in ?1")
    List<Comment> findAllByEvents(List<Long> ids);

    @Query("select c from Comment c where c.status=?1 and c.event.id in ?2")
    List<Comment> findPublishedByEvents(CommentStatus status, List<Long> ids);

    void deleteAllByAuthorId(Long authorId);
}
