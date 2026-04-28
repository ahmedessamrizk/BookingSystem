package com.demo.dtos.response;

import com.demo.entities.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String email,
        Role role,
        LocalDateTime createdAt
)
{}
