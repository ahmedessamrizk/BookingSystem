package com.demo.dtos.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoomListDto(
        UUID id,
        String name,
        int capacity,
        LocalDateTime createdAt,
        boolean deleted
)
{}

