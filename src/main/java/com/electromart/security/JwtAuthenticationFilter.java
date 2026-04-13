package com.electromart.security;

import com.electromart.config.JwtProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        List<String> tokens = extractTokens(request);
        if (!tokens.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
            boolean authenticated = false;
            for (int i = tokens.size() - 1; i >= 0; i--) {
                String token = tokens.get(i);
                if (!jwtTokenProvider.isValid(token)) {
                    continue;
                }
                try {
                    String username = jwtTokenProvider.getUsername(token);
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    request.getSession(true).setAttribute(
                            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                            SecurityContextHolder.getContext()
                    );
                    log.debug("Authenticated request {} for user {}", request.getRequestURI(), username);
                    authenticated = true;
                    break;
                } catch (RuntimeException exception) {
                    log.warn("Failed to authenticate JWT for request {}: {}", request.getRequestURI(), exception.getMessage());
                    SecurityContextHolder.clearContext();
                }
            }
            if (!authenticated) {
                clearAuthCookie(response);
            }
        }
        filterChain.doFilter(request, response);
    }

    private List<String> extractTokens(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7).trim();
            return token.isBlank() ? List.of() : List.of(token);
        }
        return extractTokensFromCookie(request);
    }

    private List<String> extractTokensFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return List.of();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> jwtProperties.cookieName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.isBlank())
                .toList();
    }

    private void clearAuthCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(jwtProperties.cookieName(), "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
