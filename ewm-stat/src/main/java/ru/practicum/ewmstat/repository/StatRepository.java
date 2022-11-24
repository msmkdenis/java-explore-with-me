package ru.practicum.ewmstat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmstat.entity.EndPointHit;

import java.time.LocalDateTime;

@Repository
public interface StatRepository extends JpaRepository<EndPointHit, Long>, JpaSpecificationExecutor<EndPointHit> {

    @Query("select count(distinct h.ip) from hits h where h.uri = ?1 and h.timestamp >= ?2 and h.timestamp <= ?3")
    long countDistinctByUri(String uri, LocalDateTime start, LocalDateTime end);

    @Query("select count(h) from hits h where h.uri = ?1 and h.timestamp >= ?2 and h.timestamp <= ?3")
    long countByUri(String uri, LocalDateTime start, LocalDateTime end);
}
