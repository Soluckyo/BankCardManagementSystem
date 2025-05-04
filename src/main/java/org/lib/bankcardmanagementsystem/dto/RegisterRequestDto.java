package org.lib.bankcardmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.lib.bankcardmanagementsystem.entity.Role;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для регистрации")
public class RegisterRequestDto {

    @Email
    @NotBlank(message = "Email не может быть пустым")
    @Schema(description = "почта", example = "mail@mail.com")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Schema(description = "пароль", example = "password")
    private String password;

    @Schema(description = "роль")
    private Role role;

}
