package org.lib.bankcardmanagementsystem.service;

import org.lib.bankcardmanagementsystem.dto.UserDto;
import org.lib.bankcardmanagementsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    User getUserById(Long userId);
    User getUserByEmail(String email);
    String deleteUserById(Long userId);
    Page<UserDto> getAllUsers(Pageable pageable);
}
