package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionJpaRepository extends JpaRepository<ChatSession, String> {

    @Query("SELECT s FROM ChatSession s WHERE s.user.userId = :userId ORDER BY s.updatedAt DESC")
    List<ChatSession> findByUserIdOrderByUpdatedAtDesc(@Param("userId") String userId);

    @Query("SELECT s FROM ChatSession s WHERE s.id = :id AND s.user.userId = :userId")
    Optional<ChatSession> findByIdAndUserId(@Param("id") String id, @Param("userId") String userId);
}
