package com.example.exam_java_salanhin.repositories;

import com.example.exam_java_salanhin.models.Basket;
import com.example.exam_java_salanhin.models.BasketItem;
import com.example.exam_java_salanhin.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {
    List<BasketItem> findByBasket(Basket basket);
    Optional<BasketItem> findByBasketAndProduct(Basket basket, Product product);
}
