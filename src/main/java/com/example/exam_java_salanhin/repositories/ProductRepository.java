package com.example.exam_java_salanhin.repositories;

import com.example.exam_java_salanhin.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p JOIN FETCH p.brand JOIN FETCH p.category")
    List<Product> findAllWithBrandAndCategory();
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productImages WHERE p.id = :productId")
    Optional<Product> findByIdWithImages(@Param("productId") Long productId);
}
