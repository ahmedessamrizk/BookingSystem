package com.demo.dtos.response;

import java.util.UUID;

public record RoomSummaryDto(
        UUID id,
        String name
)
{}
