package com.demo.dtos.response;

import com.demo.entities.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationDetailsDto(
        UUID id,
        UserSummaryDto createdBy,
        RoomSummaryDto room,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalDateTime createdAt,
        ReservationStatus status
)
{}
