package com.example.exam_java_salanhin.repositories;

import com.example.exam_java_salanhin.models.Basket;
import com.example.exam_java_salanhin.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BasketRepository extends JpaRepository<Basket, Long> {
    Optional<Basket> findByUser(User user);
    List<Basket> findByUserId(Long userId);
}
