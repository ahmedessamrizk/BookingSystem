package com.demo.dtos.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationSummaryDto (
        UUID id,
        LocalDateTime startTime,
        LocalDateTime endTime
)
{}
