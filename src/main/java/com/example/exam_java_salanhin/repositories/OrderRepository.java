package com.example.exam_java_salanhin.repositories;

import com.example.exam_java_salanhin.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
