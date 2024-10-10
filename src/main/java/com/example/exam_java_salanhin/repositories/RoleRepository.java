package com.example.exam_java_salanhin.repositories;

import com.example.exam_java_salanhin.models.Role;
import com.example.exam_java_salanhin.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
