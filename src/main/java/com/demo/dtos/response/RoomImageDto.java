package com.demo.dtos.response;

import java.util.UUID;

public record RoomImageDto(
        UUID id,
        String url
) {}
