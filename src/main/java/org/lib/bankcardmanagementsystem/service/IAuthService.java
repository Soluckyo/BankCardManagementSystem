package org.lib.bankcardmanagementsystem.service;

import org.lib.bankcardmanagementsystem.dto.RegisterRequestDto;
import org.lib.bankcardmanagementsystem.dto.TokenRequestDto;
import org.lib.bankcardmanagementsystem.dto.TokenResponseDto;

import javax.naming.AuthenticationException;

public interface IAuthService {
    void register(RegisterRequestDto request);
    TokenResponseDto refreshAccessToken(String refreshToken) throws AuthenticationException;
    TokenResponseDto signIn(TokenRequestDto tokenRequestDto);
}
