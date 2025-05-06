package org.lib.bankcardmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO пользователя")
public class UserDto {
    @Schema(description = "ID пользователя", example = "12L")
    private Long userid;

    @Schema(description = "Имя пользователя", example = "IVAN")
    private String firstName;

    @Schema(description = "Фамилия пользователя", example = "IVANOV")
    private String lastName;

    @Schema(description = "Почта пользователя", example = "mail@mail.com")
    private String email;

    @Schema(description = "Роль пользователя", example = "ADMIN")
    private String role;
}
