package com.ergoproxy.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Lastname is required")
        String lastname,

        @NotBlank(message = "Email is required")
        @Pattern(
                regexp = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$",
                message = "Invalid email format"
        )
        String email,

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 60)
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-])[A-Za-z\\d@$!%*?&._-]{8,60}$",
                message = "Password must contain uppercase, lowercase, number and special character")
        String password,

        @NotBlank(message = "Phone number is required")
        String phoneNumber
) {
}
