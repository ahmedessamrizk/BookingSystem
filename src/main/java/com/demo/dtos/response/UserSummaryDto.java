package com.demo.dtos.response;

import java.util.UUID;

public record UserSummaryDto(
        UUID id,
        String username
){}
