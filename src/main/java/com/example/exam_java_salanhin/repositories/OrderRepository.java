package com.example.exam_java_salanhin.repositories;

import com.example.exam_java_salanhin.models.Order;
import com.example.exam_java_salanhin.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<List<Order>> findByUser(User user);
    List<Order> findByUserId(Long userId);
}
