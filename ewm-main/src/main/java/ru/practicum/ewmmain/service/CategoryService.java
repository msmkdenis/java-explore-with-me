package ru.practicum.ewmmain.service;

import ru.practicum.ewmmain.dto.category.CategoryDto;
import ru.practicum.ewmmain.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto);

    void deleteCategory(long catId);

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategory(long catId);
}
