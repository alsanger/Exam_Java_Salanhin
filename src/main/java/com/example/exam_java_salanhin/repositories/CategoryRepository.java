package com.example.exam_java_salanhin.repositories;

import com.example.exam_java_salanhin.models.Category;
import com.example.exam_java_salanhin.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String categoryName);
    boolean existsByNameAndIdNot(String categoryName, Long id);
    Optional<Category> findByName(String name);
}
