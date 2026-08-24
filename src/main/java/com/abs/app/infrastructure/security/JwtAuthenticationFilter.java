package com.abs.app.infrastructure.security;
import com.abs.app.common.constant.AuthConstant;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath().toLowerCase();
        boolean skip = isPublicAuthPath(path)
                || path.startsWith("/public/")
                || path.startsWith("/images/")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars");

        log.debug("shouldNotFilter? path='{}' -> {}", path, skip);
        return skip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath().toLowerCase();
        log.debug("doFilterInternal start for path='{}'", path);

        // extra safety
        if (isPublicAuthPath(path)
                || path.startsWith("/public/")
                || path.startsWith("/images/")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")) {
            log.debug("Skipping JWT processing for swagger/auth path '{}'", path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);

        // NO TOKEN -> allow through
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtTokenProvider.validateAccessToken(token)) {
                String userId = jwtTokenProvider.getUserIdFromAccessToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

                if (!isActiveUser(userDetails)) {
                    log.debug("Authenticated token belongs to an inactive userId='{}'", userId);
                    SecurityContextHolder.clearContext();
                    writeUnauthorizedResponse(response, AuthConstant.PROHIBIT_ACCOUNT_MESSAGE);
                    return;
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Authentication set for userId='{}'", userId);
            } else {
                log.debug("Invalid/expired token");
                SecurityContextHolder.clearContext();
                writeUnauthorizedResponse(response, AuthConstant.INVALID_TOKEN);
                return;
            }
        } catch (Exception e) {
            log.error("JWT error while processing request to '{}': {}", path, e.getMessage(), e);
            SecurityContextHolder.clearContext();
            writeUnauthorizedResponse(response, AuthConstant.INVALID_TOKEN);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        log.trace("Authorization header: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("accessToken".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private boolean isActiveUser(UserDetails userDetails) {
        return userDetails.isEnabled()
                && userDetails.isAccountNonLocked()
                && userDetails.isAccountNonExpired()
                && userDetails.isCredentialsNonExpired();
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(JSON_CONTENT_TYPE);
        response.getWriter().write(String.format(
                "{\"success\":false,\"message\":\"%s\",\"data\":null}",
                escapeJson(message)));
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private boolean isPublicAuthPath(String path) {
        return path.equals("/auth/login")
                || path.equals("/auth/register")
                || path.equals("/auth/forgot-password")
                || path.equals("/auth/refresh-token")
                || path.equals("/auth/google")
                || path.equals("/auth/google/callback")
                || path.equals("/auth/reset-password")
                || path.equals("/auth/send-otp")
                || path.equals("/auth/verify-otp")
                || path.equals("/auth/logout");
    }
}
