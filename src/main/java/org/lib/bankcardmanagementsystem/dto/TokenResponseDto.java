package org.lib.bankcardmanagementsystem.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "JWT ответ")
public class TokenResponseDto {

    @Schema(description = "Токен доступа")
    private String accessToken;

    @Schema(description = "Токен обновления")
    private String refreshToken;
}
