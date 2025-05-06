package org.lib.bankcardmanagementsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lib.bankcardmanagementsystem.entity.Role;
import org.lib.bankcardmanagementsystem.entity.User;
import org.lib.bankcardmanagementsystem.exception.UserNotFoundException;
import org.lib.bankcardmanagementsystem.repository.UserRepository;
import org.lib.bankcardmanagementsystem.service.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetUserByIdSuccess() {
        User mockUser = User.builder()
                .idUser(1L)
                .email("test@mail.com")
                .password("password")
                .role(Role.USER)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        User result = userService.getUserById(1L);
        assertNotNull(result);
        assertEquals("test@mail.com", result.getEmail());
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void testGetUserByEmailSuccess() {
        User mockUser = User.builder()
                .idUser(2L)
                .email("test@mail.com")
                .password("password")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(mockUser));

        User result = userService.getUserByEmail("test@mail.com");
        assertNotNull(result);
        assertEquals(2L, result.getIdUser());
    }

    @Test
    void testGetUserByEmailNotFound() {
        when(userRepository.findByEmail("notfound@mail.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("notfound@mail.com"));
    }
}
