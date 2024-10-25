package com.example.exam_java_salanhin.services.admin;

import com.example.exam_java_salanhin.models.Category;
import com.example.exam_java_salanhin.models.Product;
import com.example.exam_java_salanhin.repositories.CategoryRepository;
import com.example.exam_java_salanhin.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElse(null);
    }

    public void saveCategory(Category category) {
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            return;
        }

        categoryRepository.save(category);
    }

    public void updateCategory(Long categoryId, String categoryName) {
        if (categoryRepository.existsByCategoryNameAndIdNot(categoryName, categoryId)) {
            return;
        }

        Category category = getCategoryById(categoryId);
        if (category != null) {
            category.setCategoryName(categoryName);
            categoryRepository.save(category);
        }
    }

    public void deleteCategoryById(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
