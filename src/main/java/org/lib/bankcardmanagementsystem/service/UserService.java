package org.lib.bankcardmanagementsystem.service;

import org.lib.bankcardmanagementsystem.entity.User;
import org.lib.bankcardmanagementsystem.exception.UserNotFoundException;
import org.lib.bankcardmanagementsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Такой пользователь не найден!"));
    }
}
