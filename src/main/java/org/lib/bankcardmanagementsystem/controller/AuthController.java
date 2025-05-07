package org.lib.bankcardmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.lib.bankcardmanagementsystem.dto.RefreshTokenDTO;
import org.lib.bankcardmanagementsystem.dto.RegisterRequestDto;
import org.lib.bankcardmanagementsystem.dto.TokenRequestDto;
import org.lib.bankcardmanagementsystem.dto.TokenResponseDto;
import org.lib.bankcardmanagementsystem.service.IAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "auth-controller")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "200", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "409", description = "Пароль введен не верно!")
    })
    @Operation(
            summary = "Войти в систему",
            description = "Аутентифицирует пользователя"
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> signIn(@RequestBody TokenRequestDto tokenRequestDto) {
        TokenResponseDto tokenResponseDto = authService.signIn(tokenRequestDto);
        return ResponseEntity.ok(tokenResponseDto);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Невалидный токен!"),
            @ApiResponse(responseCode = "200", description = "Пользователь успешно удален"),
    })
    @Operation(
            summary = "Обновить токен",
            description = "обновляет AccessToken на основе refreshToken'a "
    )
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestBody RefreshTokenDTO refreshToken) {
        try {
            TokenResponseDto jwtResponseDTO = authService.refreshAccessToken(refreshToken.getRefreshToken());
            return ResponseEntity.ok(jwtResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "409", description = "Почта уже используется!")
    })
    @Operation(
            summary = "Регистрация пользователя",
            description = "Создает нового пользователя"
    )
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDto request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }
}
