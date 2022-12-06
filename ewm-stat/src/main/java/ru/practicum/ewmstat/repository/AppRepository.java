package ru.practicum.ewmstat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmstat.entity.App;

@Repository
public interface AppRepository extends JpaRepository<App, Long> {
}
