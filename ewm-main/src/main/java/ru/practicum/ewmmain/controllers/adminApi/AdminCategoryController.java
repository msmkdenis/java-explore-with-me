package ru.practicum.ewmmain.controllers.adminApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.dto.category.CategoryDto;
import ru.practicum.ewmmain.dto.category.NewCategoryDto;
import ru.practicum.ewmmain.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto addCategory(
            @RequestBody @Valid NewCategoryDto newCategoryDto
    ) {
        log.info("adminCategoryController POST addCategory получен NewCategoryDto: {}", newCategoryDto);
        return categoryService.addCategory(newCategoryDto);
    }

    @PatchMapping
    public CategoryDto updateCategory(
            @RequestBody @Valid CategoryDto categoryDto
    ) {
        log.info("adminCategoryController PATCH updateCategory получен CategoryDto: {}", categoryDto);
        return categoryService.updateCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    public void deleteCategory(@PathVariable long catId) {
        log.info("adminCategoryController DELETE deleteCategory получен catId: {}", catId);
        categoryService.deleteCategory(catId);
    }
}
