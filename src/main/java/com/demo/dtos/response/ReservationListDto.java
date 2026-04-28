package com.demo.dtos.response;

import com.demo.entities.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationListDto (
    UUID id,
    RoomSummaryDto room,
    LocalDateTime startTime,
    LocalDateTime endTime,
    ReservationStatus status
)
{}
