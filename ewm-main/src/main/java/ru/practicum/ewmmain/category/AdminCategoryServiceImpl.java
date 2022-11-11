package ru.practicum.ewmmain.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.exception.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category newCategory = CategoryMapper.toCategory(newCategoryDto);
        log.info("newCategory after mapping: {}", newCategory);

        newCategory = categoryRepository.save(newCategory);
        log.info("newCategory after save: {}", newCategory);

        return CategoryMapper.toCategoryDto(newCategory);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = checkCategory(categoryDto.getId());
        category.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(category);
    }

    // добавить проверку наличия связанных events
    @Override
    public void deleteCategory(long catId) {
        checkCategory(catId);
        categoryRepository.deleteById(catId);
    }

    private Category checkCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Category id=%d не найден!", id)));
    }

    private int getPageNumber(int from, int size) {
        return from / size;
    }
}
