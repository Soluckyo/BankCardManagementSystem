package org.lib.bankcardmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.lib.bankcardmanagementsystem.dto.UserDto;
import org.lib.bankcardmanagementsystem.entity.User;
import org.lib.bankcardmanagementsystem.mapper.UserMapper;
import org.lib.bankcardmanagementsystem.service.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "user-controller")
@RestController
@RequestMapping("/user")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Пользователи не найдены"),
            @ApiResponse(responseCode = "200", description = "Пользователи успешно найдены"),
            @ApiResponse(responseCode = "403", description = "Не хватает прав доступа!")
    })
    @Operation(
            summary = "Получение всех пользователей",
            description = "Возвращает всех существующих пользователей. Только для администратора!"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-user")
    public ResponseEntity<Page<UserDto>> getAllUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "200", description = "Пользователь успешно найден"),
            @ApiResponse(responseCode = "403", description = "Не хватает прав доступа!")
    })
    @Operation(
            summary = "Получение пользователя по ID",
            description = "Возвращает пользователя по ID. Только для администратора!"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {

        User user = userService.getUserById(userId);
        return UserMapper.toUserDto(user);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "200", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "403", description = "Не хватает прав доступа!")
    })
    @Operation(
            summary = "Удаление пользователя по ID",
            description = "Удаление пользователя по ID. Только для администратора!"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok("User deleted successfully.");
    }
}
