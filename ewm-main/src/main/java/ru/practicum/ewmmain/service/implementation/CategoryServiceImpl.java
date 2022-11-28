package ru.practicum.ewmmain.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.dto.category.CategoryDto;
import ru.practicum.ewmmain.dto.category.NewCategoryDto;
import ru.practicum.ewmmain.dto.mapper.CategoryMapper;
import ru.practicum.ewmmain.entity.Category;
import ru.practicum.ewmmain.exception.ConflictError;
import ru.practicum.ewmmain.exception.EntityNotFoundException;
import ru.practicum.ewmmain.repository.CategoryRepository;
import ru.practicum.ewmmain.repository.EventRepository;
import ru.practicum.ewmmain.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category newCategory = CategoryMapper.toCategory(newCategoryDto);
        newCategory = categoryRepository.save(newCategory);

        return CategoryMapper.toCategoryDto(newCategory);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = checkCategory(categoryDto.getId());
        category.setName(categoryDto.getName());

        return CategoryMapper.toCategoryDto(category);
    }

    @Transactional
    @Override
    public void deleteCategory(long catId) {
        Category category = checkCategory(catId);
        checkCategoryEvents(catId);
        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        return categoryRepository.findAll(PageRequest.of(getPageNumber(from, size), size)).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(long catId) {
        Category category = checkCategory(catId);

        return CategoryMapper.toCategoryDto(category);
    }

    private Category checkCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Category id=%d не найден!", id)));
    }

    private void checkCategoryEvents(long catId) {
        if (eventRepository.areEventsWithCategory(catId)) {
            throw new ConflictError(String.format("У категории id=%d есть связанные события!", catId));
        }
    }

    private int getPageNumber(int from, int size) {
        return from / size;
    }
}
