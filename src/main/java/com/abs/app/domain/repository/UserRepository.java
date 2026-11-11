package com.abs.app.domain.repository;

import com.abs.app.domain.entity.User;

import com.abs.app.domain.entity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String userName);

    boolean existsByEmail(String email);

    Optional<User> findById(String id);

    void save(User user);

    long count();

    Page<User> search(String keyword, UserStatus status, Pageable pageable);
}
