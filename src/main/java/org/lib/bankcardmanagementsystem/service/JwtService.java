package org.lib.bankcardmanagementsystem.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.lib.bankcardmanagementsystem.dto.TokenResponseDto;
import org.lib.bankcardmanagementsystem.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
/**
 * Сервис для работы с JWT.
 * Генерация, обновление, складывание в ответ, получения информации из токена
 */
@Service
@Slf4j
public class JwtService implements IJwtService{

    /**
     * Секретный ключ.
     * Значение загружается из application.properties по ключу `jwt.secret.key`.
     */
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    /**
     * Время жизни токена доступа.
     * Значение загружается из application.properties по ключу `jwt.token.access.expiration.minutes`.
     */
    @Value("${jwt.token.access.expiration.minutes}")
    private Long ACCESS_TOKEN_EXPIRATION_MINUTES;

    /**
     * Время жизни токена обновления.
     * Значение загружается из application.properties по ключу `jwt.token.refresh.expiration.days`.
     */
    @Value("${jwt.token.refresh.expiration.days}")
    private Long REFRESH_TOKEN_EXPIRATION_DAYS;

    /**
     * Генерирует токен доступа
     *
     * @param user пользователь
     * @return строку с токеном
     */
    public String generateAccessToken(User user) {
        Date dateEx = Date.from(LocalDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRATION_MINUTES)
                .atZone(ZoneId.systemDefault()).toInstant());
        Date now = new Date();
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(now)
                .setExpiration(dateEx)
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Генерирует токен обновления
     *
     * @param user пользователь
     * @return строку с токеном обновления
     */
    public String generateRefreshToken(User user) {
            Date dateEx = Date.from(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRATION_DAYS)
                    .atZone(ZoneId.systemDefault()).toInstant());
            Date now = new Date();
            return Jwts.builder()
                    .setSubject(user.getEmail())
                    .claim("role", user.getRole())
                    .setIssuedAt(now)
                    .setExpiration(dateEx)
                    .signWith(getSignInKey())
                    .compact();
    }

    /**
     * Валидирует токен
     *
     * @param token токен доступа или токен обновления
     * @return true, если токен валидный
     *         false, если токен невалидный
     */
    public Boolean validateToken(String token) {
        try {
            log.debug("Trying to validate token: {}", token);
            claimsToken(token);
            log.debug("Trying to validate token: {}", token);
            return true;
        }catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
            return false;
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
            return false;
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
            return false;
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
            return false;
        } catch (Exception e) {
            log.error("Invalid token", e);
            return false;
        }
    }

    /**
     * Достает Claims из токена доступа
     *
     * @param token токен доступа
     * @return Claims
     */
    public Claims claimsToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Получает почту пользователя из токена
     *
     * @param token токен доступа
     * @return строку с почтой
     */
    public String getEmailFromToken(String token) {
        return claimsToken(token).getSubject();
    }

    /**
     * Получает роль пользователя из токена
     *
     * @param token токен доступа
     * @return  строку с ролью
     */
    public String getRoleFromToken(String token) {
        return claimsToken(token).get("role", String.class);
    }

    /**
     * Складывает токены в DTO для ответа и отдает DTO
     * @param user пользователь
     * @return TokenResponseDto, хранящий в себе токены
     */
    public TokenResponseDto generateTokenResponse(User user) {
        TokenResponseDto tokenResponseDto = new TokenResponseDto();
        tokenResponseDto.setAccessToken(generateAccessToken(user));
        tokenResponseDto.setRefreshToken(generateRefreshToken(user));
        return tokenResponseDto;
    }

    /**
     * Обновляет токен доступа с помощью токена обновления.
     * Токен обновления не меняется!
     *
     * @param user пользователь
     * @return TokenResponseDto, хранящий в себе токены
     */
    public TokenResponseDto refreshAccessToken(User user, String refreshToken) {
        TokenResponseDto tokenResponseDto = new TokenResponseDto();
        tokenResponseDto.setRefreshToken(refreshToken);
        tokenResponseDto.setAccessToken(generateAccessToken(user));
        return tokenResponseDto;
    }

    /**
     * Возвращает секретный ключ и декодирует его.
     * Значение загружается из application.properties по ключу `jwt.secret.key`.
     * @return секретный ключ
     */
    public SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }
}
