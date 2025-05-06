package org.lib.bankcardmanagementsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lib.bankcardmanagementsystem.dto.RegisterRequestDto;
import org.lib.bankcardmanagementsystem.dto.TokenRequestDto;
import org.lib.bankcardmanagementsystem.dto.TokenResponseDto;
import org.lib.bankcardmanagementsystem.entity.Role;
import org.lib.bankcardmanagementsystem.entity.User;
import org.lib.bankcardmanagementsystem.exception.EmailAlreadyExistsException;
import org.lib.bankcardmanagementsystem.repository.UserRepository;
import org.lib.bankcardmanagementsystem.service.AuthService;
import org.lib.bankcardmanagementsystem.service.JwtService;
import org.lib.bankcardmanagementsystem.service.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    RegisterRequestDto registerRequestDto;
    User mockUser;
    String refreshToken;
    String accessToken;
    TokenResponseDto tokenResponseDto;

    @BeforeEach
    void setUp() {
        registerRequestDto = new RegisterRequestDto("test@mail.com", "password", Role.ADMIN);
        mockUser = User.builder()
                .email("user@mail.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
        refreshToken = "refreshToken";
        accessToken = "accessToken";
        tokenResponseDto = new TokenResponseDto(accessToken, refreshToken);
    }


    @Test
    void testRegisterSuccess() {
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        authService.register(registerRequestDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterEmailAlreadyExists() {
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(registerRequestDto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRefreshAccessTokenSuccess() throws AuthenticationException {
        String refreshToken = "valid.refresh.token";
        User user = new User();
        user.setEmail("test@mail.com");

        when(jwtService.validateToken(refreshToken)).thenReturn(true);
        when(jwtService.getEmailFromToken(refreshToken)).thenReturn("test@mail.com");
        when(userService.getUserByEmail("test@mail.com")).thenReturn(user);
        when(jwtService.refreshAccessToken(user, refreshToken)).thenReturn(tokenResponseDto);

        TokenResponseDto result = authService.refreshAccessToken(refreshToken);

        assertNotNull(result);
        assertEquals("accessToken", result.getAccessToken());
        assertEquals("refreshToken", result.getRefreshToken());
    }

    @Test
    void testRefreshAccessTokenThrowsException() {
        String refreshToken = "invalid.token";

        when(jwtService.validateToken(refreshToken)).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> authService.refreshAccessToken(refreshToken));
    }

    @Test
    void testSignInSuccess(){
        TokenRequestDto requestDto = new TokenRequestDto("user@mail.com", "password");

        when(userService.getUserByEmail("user@mail.com")).thenReturn(mockUser);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtService.generateTokenResponse(mockUser)).thenReturn(tokenResponseDto);

        TokenResponseDto result = authService.signIn(requestDto);

        assertNotNull(result);
        assertEquals("accessToken", result.getAccessToken());
        assertEquals("refreshToken", result.getRefreshToken());
    }

    @Test
    void testSignInThrowsException() {
        TokenRequestDto requestDto = new TokenRequestDto("user@mail.com", "password");

        when(userService.getUserByEmail("user@mail.com")).thenReturn(mockUser);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.signIn(requestDto));
    }



}
