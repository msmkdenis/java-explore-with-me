package ru.practicum.ewmmain.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @PostMapping
    public CategoryDto addCategory(
            @RequestBody @Valid NewCategoryDto newCategoryDto
    ) {
        log.info("adminCategoryController POST addCategory получен NewCategoryDto: {}", newCategoryDto);
        return adminCategoryService.addCategory(newCategoryDto);
    }

    @PatchMapping
    public CategoryDto updateCategory(
            @RequestBody @Valid CategoryDto categoryDto
    ) {
        log.info("adminCategoryController PATCH updateCategory получен CategoryDto: {}", categoryDto);
        return adminCategoryService.updateCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    public void deleteCategory(@PathVariable long catId) {
        log.info("adminCategoryController DELETE deleteCategory получен catId: {}", catId);
        adminCategoryService.deleteCategory(catId);
    }
}
