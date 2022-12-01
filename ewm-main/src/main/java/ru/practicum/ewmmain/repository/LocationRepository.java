package ru.practicum.ewmmain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmmain.entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}
