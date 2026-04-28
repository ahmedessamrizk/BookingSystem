package com.demo.dtos.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoomDto (
        UUID id,
        String name,
        int capacity,
        LocalDateTime createdAt
)
{}

