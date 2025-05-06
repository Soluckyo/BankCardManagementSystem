package org.lib.bankcardmanagementsystem.service;

import org.lib.bankcardmanagementsystem.dto.UserDto;
import org.lib.bankcardmanagementsystem.entity.User;
import org.lib.bankcardmanagementsystem.exception.UserNotFoundException;
import org.lib.bankcardmanagementsystem.mapper.UserMapper;
import org.lib.bankcardmanagementsystem.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * Сервис для работы с пользователями
 */
@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Метод возвращает пользователя по ID
     *
     * @return пользователь с заданным ID
     * @param userId числовое значение представляющее собой ID пользователя
     * @throws UserNotFoundException выбрасывается, если пользователь с таким ID не найден
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Такой пользователь не найден!"));
    }

    /**
     * Метод возвращает пользователя по ID
     *
     * @return пользователь с заданным ID
     * @param email строка, имя пользователя
     * @throws UserNotFoundException выбрасывается, если пользователь с таким email не найден
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Такой пользователь не найден!"));
    }

    /**
     * Удаляет пользователя по ID
     * @param userId ID пользователя
     */
    public String deleteUserById(Long userId) {
        if(!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь не существует!");
        }
        userRepository.deleteById(userId);
        return "Пользователь успешно удален";
    }

    /**
     * Возвращает всех пользователей постранично
     *
     * @param pageable параметры страницы
     * @return страницу с пользователями
     */
    public Page<UserDto> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserMapper::toUserDto);
    }
}
