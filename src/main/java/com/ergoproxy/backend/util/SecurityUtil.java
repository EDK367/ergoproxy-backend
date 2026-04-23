package com.ergoproxy.backend.util;

import com.ergoproxy.backend.exception.CustomException;
import com.ergoproxy.backend.model.User;
import com.ergoproxy.backend.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class SecurityUtil {

    private SecurityUtil() {
    }

    public static User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new CustomException(
                    HttpStatus.UNAUTHORIZED,
                    "User not authenticated"
            );
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }

    public static boolean isOwner(java.util.UUID resourceOwnerId) {
        return getAuthenticatedUser().getId().equals(resourceOwnerId);
    }

    public static java.util.UUID getAuthenticatedUserId() {
        return getAuthenticatedUser().getId();
    }

    public static String getAuthenticatedUsername() {
        return getAuthenticatedUser().getUsername();
    }
}
