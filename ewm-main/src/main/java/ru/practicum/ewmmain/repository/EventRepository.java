package ru.practicum.ewmmain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmmain.entity.Event;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    @Query("select e from Event e where e.id in ?1")
    Set<Event> getEventsByIds(Collection<Long> ids);

    @Query("select (count(e) > 0) from Event e where e.category.id = ?1")
    boolean areEventsWithCategory(Long catId);
}
