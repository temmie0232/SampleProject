package com.example.simplelibrary.category;

import com.example.simplelibrary.category.dto.CategoryRequest;
import com.example.simplelibrary.category.dto.CategoryResponse;
import com.example.simplelibrary.exception.ConflictException;
import com.example.simplelibrary.exception.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryResponse(category.getId(), category.getName(), category.isActive()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new ConflictException("Category name already exists");
        }
        Category category = new Category();
        category.setName(request.getName());
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }
        Category saved = categoryRepository.save(category);
        return new CategoryResponse(saved.getId(), saved.getName(), saved.isActive());
    }

    @Transactional
    public CategoryResponse update(String id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        if (request.getName() != null && !request.getName().equalsIgnoreCase(category.getName())) {
            if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
                throw new ConflictException("Category name already exists");
            }
            category.setName(request.getName());
        }
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }
        return new CategoryResponse(category.getId(), category.getName(), category.isActive());
    }

    @Transactional
    public void delete(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        categoryRepository.delete(category);
    }
}

