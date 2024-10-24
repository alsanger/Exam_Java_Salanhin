package com.example.exam_java_salanhin.repositories;

import com.example.exam_java_salanhin.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByCategoryName(String categoryName);
    boolean existsByCategoryNameAndIdNot(String categoryName, Long id);
}
