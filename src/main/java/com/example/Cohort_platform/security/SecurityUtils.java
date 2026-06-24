package com.example.Cohort_platform.security;

import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static UserPrincipal currentUser() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Long currentUserId() {
        return currentUser().getId();
    }

    public static String currentRole() {
        return currentUser().getRole();
    }
}
