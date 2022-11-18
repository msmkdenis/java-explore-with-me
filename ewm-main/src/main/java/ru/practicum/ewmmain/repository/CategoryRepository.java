package ru.practicum.ewmmain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
