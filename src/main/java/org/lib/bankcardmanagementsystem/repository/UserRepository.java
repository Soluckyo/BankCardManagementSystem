package org.lib.bankcardmanagementsystem.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.lib.bankcardmanagementsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(@Email @NotBlank(message = "Email не может быть пустым") String email);

    Optional<User> findByEmail(String email);
}
