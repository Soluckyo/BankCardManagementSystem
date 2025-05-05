package org.lib.bankcardmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Токен обновления")
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDTO {

    @Schema(description = "Токен обновления")
    private String refreshToken;
}
