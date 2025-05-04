package org.lib.bankcardmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "JWT запрос")
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequestDto {

    @Schema(description = "Почта", example = "user@gmail.com")
    private String email;

    @Schema(description = "Пароль", example = "password")
    private String password;
}
