package org.lib.bankcardmanagementsystem.service;

import io.jsonwebtoken.Claims;
import org.lib.bankcardmanagementsystem.dto.TokenResponseDto;
import org.lib.bankcardmanagementsystem.entity.User;

import javax.crypto.SecretKey;

public interface IJwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    Boolean validateToken(String token);
    Claims claimsToken(String token);
    String getEmailFromToken(String token);
    String getRoleFromToken(String token);
    TokenResponseDto generateTokenResponse(User user);
    TokenResponseDto refreshAccessToken(User user, String refreshToken);
    SecretKey getSignInKey();

}
