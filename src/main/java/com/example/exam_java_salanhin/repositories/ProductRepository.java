package com.example.exam_java_salanhin.repositories;

import com.example.exam_java_salanhin.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
