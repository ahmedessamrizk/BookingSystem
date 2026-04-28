package com.demo.dtos.response;

import lombok.Builder;

@Builder
public record LoginResponse(
        String token
)
{}
