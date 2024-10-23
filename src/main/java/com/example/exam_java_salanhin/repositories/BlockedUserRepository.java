package com.example.exam_java_salanhin.repositories;

import com.example.exam_java_salanhin.models.BlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockedUserRepository extends JpaRepository<BlockedUser, Long> {
    Optional<BlockedUser> findByUserId(Long userId);
}
