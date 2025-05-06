package org.lib.bankcardmanagementsystem.service;

import org.lib.bankcardmanagementsystem.dto.RegisterRequestDto;
import org.lib.bankcardmanagementsystem.dto.TokenRequestDto;
import org.lib.bankcardmanagementsystem.dto.TokenResponseDto;
import org.lib.bankcardmanagementsystem.entity.User;
import org.lib.bankcardmanagementsystem.exception.EmailAlreadyExistsException;
import org.lib.bankcardmanagementsystem.exception.UserNotFoundException;
import org.lib.bankcardmanagementsystem.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

@Service
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IJwtService jwtService;
    private final UserService userService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, IJwtService jwtService, UserService userService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    /**
     * Сохраняет пользователя в базу данных
     *
     * @param request DTO, содержащее в себе email, password, role
     * @throws EmailAlreadyExistsException выбрасывается, если email уже используется
     */
    public void register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException("Такой Email уже используется");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);
    }

    /**
     * Обновляет токен доступа
     *
     * @param refreshToken токен обновления
     * @return TokenResponseDto, содержащий токены
     * @throws AuthenticationException выбрасывается при невалидном токене
     */
    public TokenResponseDto refreshAccessToken(String refreshToken) throws AuthenticationException {
        if(refreshToken != null && jwtService.validateToken(refreshToken)){
            User user = userService.getUserByEmail(jwtService.getEmailFromToken(refreshToken));
            return jwtService.refreshAccessToken(user, refreshToken);
        }else {
            throw new AuthenticationException("Невалидный refresh токен");
        }
    }

    /**
     * Выдает токены доступа и обновления
     *
     * @param tokenRequestDto запрос с учетными данными
     * @return TokenResponseDto, содержащий токены
     */
    public TokenResponseDto signIn(TokenRequestDto tokenRequestDto){
        User user = findByCredentials(tokenRequestDto);
        return jwtService.generateTokenResponse(user);
    }

    /**
     * Проверяет валидность учетных данных
     *
     * @param tokenRequestDto запрос с учетными данными
     * @return пользователя или в слу
     * @throws BadCredentialsException выбрасывается, если пароль не валидируется с сохраненным паролем
     */
    private User findByCredentials(TokenRequestDto tokenRequestDto){
        User user = userService.getUserByEmail(tokenRequestDto.getEmail());
        if(!passwordEncoder.matches(tokenRequestDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Невалидный пароль");
        }else {
            return user;
        }
    }
}
