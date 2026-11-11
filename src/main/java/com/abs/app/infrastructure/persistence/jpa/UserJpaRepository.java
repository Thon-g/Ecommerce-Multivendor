package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String userName);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.userId = :userId")
    Optional<User> findByIdWithRole(@Param("userId") String userId);

    @Query("""
        SELECT u FROM User u 
        WHERE NOT EXISTS (
            SELECT 1 FROM u.roles r WHERE r.roleName = com.abs.app.domain.entity.enums.RoleUser.ADMIN
        )
        AND (:keyword IS NULL OR :keyword = '' 
            OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR u.phoneNumber LIKE CONCAT('%', :keyword, '%'))
        AND (:status IS NULL OR u.status = :status)
    """)
    Page<User> search(
            @Param("keyword") String keyword, 
            @Param("status") UserStatus status, 
            Pageable pageable);
}
