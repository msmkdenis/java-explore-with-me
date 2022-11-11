package ru.practicum.ewmmain.category;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CategoryMapper {

    Category toCategory(NewCategoryDto newCategoryDto) {
        return new Category(
                null,
                newCategoryDto.getName());
    }

    CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName());
    }
}
