package com.example.exam_java_salanhin.repositories;

import com.example.exam_java_salanhin.models.Order;
import com.example.exam_java_salanhin.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrder(Order order);
    void deleteByOrderId(Long orderId);
}
