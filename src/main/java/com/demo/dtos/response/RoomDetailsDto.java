package com.demo.dtos.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RoomDetailsDto(
        UUID id,
        String name,
        int capacity,
        UserSummaryDto createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<RoomImageDto> images,
        boolean deleted
)
{}
