package org.lib.bankcardmanagementsystem.repository;

import org.lib.bankcardmanagementsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
