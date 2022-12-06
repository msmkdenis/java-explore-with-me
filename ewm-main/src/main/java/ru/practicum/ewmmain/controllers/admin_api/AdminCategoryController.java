package ru.practicum.ewmmain.controllers.admin_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.dto.category.CategoryDto;
import ru.practicum.ewmmain.dto.category.NewCategoryDto;
import ru.practicum.ewmmain.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto addCategory(
            @RequestBody @Valid NewCategoryDto newCategoryDto
    ) {
        log.info("adminCategoryController POST addCategory получен NewCategoryDto: {}", newCategoryDto);
        return categoryService.add(newCategoryDto);
    }

    @PatchMapping
    public CategoryDto updateCategory(
            @RequestBody @Valid CategoryDto categoryDto
    ) {
        log.info("adminCategoryController PATCH updateCategory получен CategoryDto: {}", categoryDto);
        return categoryService.update(categoryDto);
    }

    @DeleteMapping("/{catId}")
    public void deleteCategory(@PathVariable long catId) {
        log.info("adminCategoryController DELETE deleteCategory получен catId: {}", catId);
        categoryService.delete(catId);
    }
}
