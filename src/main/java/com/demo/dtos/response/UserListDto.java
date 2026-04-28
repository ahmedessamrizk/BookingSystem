package com.demo.dtos.response;

import com.demo.entities.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserListDto(
        UUID id,
        String username,
        String email,
        LocalDateTime createdAt,
        Boolean deleted,
        Role role
)
{}
