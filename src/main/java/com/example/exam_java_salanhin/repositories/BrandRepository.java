package com.example.exam_java_salanhin.repositories;

import com.example.exam_java_salanhin.models.Brand;
import com.example.exam_java_salanhin.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    boolean existsByName(String brandName);
    boolean existsByNameAndIdNot(String brandName, Long id);
    Optional<Brand> findByName(String name);
}
