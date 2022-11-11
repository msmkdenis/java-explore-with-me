package ru.practicum.ewmmain.category;

public interface AdminCategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto);

    void deleteCategory(long catId);
}
