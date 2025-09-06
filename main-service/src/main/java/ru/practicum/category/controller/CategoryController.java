package ru.practicum.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Start creating category: {}", newCategoryDto);
        CategoryDto category = categoryService.save(newCategoryDto);
        log.info("Finish creating category: {}", category);
        return category;
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCateory(@PathVariable @Positive Long catId) {
        log.info("Start delete category id={}", catId);
        categoryService.delete(catId);
    }

    @PatchMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable @Positive Long catId,
                                      @RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Start updating category {}, id= {}", newCategoryDto, catId);
        CategoryDto category = categoryService.update(catId, newCategoryDto);
        log.info("Finish updating category {}, id= {}", newCategoryDto, catId);
        return category;
    }

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Start getting categories:from={}, size={}", from, size);
        return categoryService.getCategories(from, size);

    }

    @GetMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategory(@PathVariable @Positive Long catId) {
        log.info("Start getting category:from={}, size={}", catId);
        CategoryDto category = categoryService.getCategory(catId);
        return category;
    }
}
