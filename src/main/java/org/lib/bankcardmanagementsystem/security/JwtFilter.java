package org.lib.bankcardmanagementsystem.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.lib.bankcardmanagementsystem.service.IJwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Фильтр JWT, обрабатывающий каждый входящий HTTP-запрос один раз.
 * Проверяет наличие и валидность JWT-токена в заголовке Authorization.
 * При успешной валидации извлекает email и роль пользователя из токена
 * и устанавливает их в {@link org.springframework.security.core.context.SecurityContextHolder}.
 * <p>
 * В случае недействительного или просроченного токена возвращает HTTP 401 Unauthorized.
 */
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final IJwtService jwtService;

    public JwtFilter(IJwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Основная логика фильтрации. Проверяет наличие и валидность токена,
     * при успешной проверке сохраняет данные пользователя в SecurityContext.
     *
     * @param request     входящий HTTP-запрос
     * @param response    HTTP-ответ
     * @param filterChain цепочка фильтров
     * @throws ServletException в случае ошибки фильтрации
     * @throws IOException      в случае ошибки ввода/вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        if (token != null) {
            try {
                jwtService.validateToken(token);
                setCustomUserDetailsToSecurityContextHolder(token);
            } catch (ExpiredJwtException e) {
                log.warn("JWT token expired: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"JWT token expired\"}");
                return;
            } catch (JwtException e) {
                log.warn("Invalid JWT token: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid JWT token\"}");
                return;
            }

        }
        filterChain.doFilter(request, response);
    }

    /**
     * Устанавливает email и роль пользователя, извлечённые из токена,
     * в {@link SecurityContextHolder} в виде аутентификации.
     *
     * @param token валидный JWT-токен
     */
    private void setCustomUserDetailsToSecurityContextHolder(String token) {
        String email = jwtService.getEmailFromToken(token);
        String role = jwtService.getRoleFromToken(token);

        log.info("Authenticated User: {} with Role: {}", email, role);

        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email,null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Извлекает JWT-токен из заголовка Authorization.
     * Ожидается формат: "Bearer &lt;token&gt;".
     *
     * @param request HTTP-запрос
     * @return строка токена или null, если токен отсутствует или некорректный
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
