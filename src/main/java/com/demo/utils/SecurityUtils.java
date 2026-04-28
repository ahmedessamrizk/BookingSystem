package com.demo.utils;

import com.demo.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {}

    // 🔹 Get Authentication safely
    public static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    // 🔹 Get current UserPrincipal
    public static Optional<UserPrincipal> getCurrentUser() {
        return getAuthentication()
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof UserPrincipal)
                .map(principal -> (UserPrincipal) principal);
    }

    // 🔹 Get current user ID
    public static UUID getCurrentUserId() {
        return getCurrentUser()
                .map(UserPrincipal::getId)
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
    }

    // 🔹 Get current user email
    public static String getCurrentUserEmail() {
        return getCurrentUser()
                .map(UserPrincipal::getUsername)
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
    }

    // 🔹 Check if current user has specific role
    public static boolean hasRole(String role) {
        return getCurrentUser()
                .map(user -> user.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals(role)))
                .orElse(false);
    }
}
