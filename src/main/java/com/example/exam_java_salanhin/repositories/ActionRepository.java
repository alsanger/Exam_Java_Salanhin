package com.example.exam_java_salanhin.repositories;

import com.example.exam_java_salanhin.models.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {
}
