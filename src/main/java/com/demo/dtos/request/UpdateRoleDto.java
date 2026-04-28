package com.demo.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public record UpdateRoleDto(
        @NotNull(message = "Role field is required")
        @Pattern(regexp = "ROLE_USER|ROLE_ADMIN", message = "Allowed roles only [ROLE_USER, ROLE_ADMIN]")
        String role
)
{}
