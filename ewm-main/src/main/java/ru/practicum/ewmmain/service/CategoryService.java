package ru.practicum.ewmmain.service;

import ru.practicum.ewmmain.dto.category.CategoryDto;
import ru.practicum.ewmmain.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto add(NewCategoryDto newCategoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void delete(long catId);

    List<CategoryDto> getAll(int from, int size);

    CategoryDto getById(long catId);
}
