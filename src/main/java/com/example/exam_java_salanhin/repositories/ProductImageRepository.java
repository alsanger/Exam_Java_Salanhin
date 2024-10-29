package com.example.exam_java_salanhin.repositories;

import com.example.exam_java_salanhin.models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    Optional<ProductImage> findById(Long id);
    List<ProductImage> findByProductId(Long productId);
}
