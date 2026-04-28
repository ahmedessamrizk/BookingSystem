package com.demo.dtos.request;

import jakarta.validation.constraints.*;

public record UpdateRoomRequest(
        @Size(min = 3, max = 100, message = "name must be between 3 and 100 characters")
        String name,

        @Min(value = 1, message = "Capacity must be at least 1")
        @Max(value = 1000, message = "Capacity cannot exceed 1000")
        Integer capacity
)
{}
