package com.abs.app.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public class SecurityUtils {
    private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);

    private SecurityUtils() {
    }

    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("No authenticated user in SecurityContext");
            return null;
        }
        return authentication.getName();
    }

    public static String getCurrentEmail() {
        CustomUserPrincipal principal = getCurrentUserPrincipal();
        return principal != null ? principal.getEmail() : null;
    }

    public static String getCurrentUsername() {
        CustomUserPrincipal principal = getCurrentUserPrincipal();
        return principal != null ? principal.getUserName() : null;
    }

    public static CustomUserPrincipal getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserPrincipal customUserPrincipal) {
            return customUserPrincipal;
        }

        log.debug("Principal is not CustomUserPrincipal: {}", principal.getClass().getSimpleName());
        return null;
    }

    @Deprecated
    public static String getCurrentUsernameOld() {
        return getCurrentUserId();
    }

    public static boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + roleName));
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}
