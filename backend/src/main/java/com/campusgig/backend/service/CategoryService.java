package com.campusgig.backend.service;

import com.campusgig.backend.entity.Category;
import com.campusgig.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(String name, String description) {
        if (categoryRepository.existsByName(name)) {
            throw new RuntimeException("Category already exists: " + name);
        }
        Category category = Category.builder()
                .name(name)
                .description(description)
                .build();
        return categoryRepository.save(category);
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }
}
