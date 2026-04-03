package com.stockpro.auth.repository;

import com.stockpro.auth.domain.User;
import com.stockpro.auth.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(UserRole role);

    List<User> findByActiveTrue();

    List<User> findByRoleInAndActiveTrue(Collection<UserRole> roles);
}