package com.demo.utils;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private boolean success;
    private String message;
    private String errorCode;
    private List<FieldError> errors;

    public static ApiErrorResponse of(String message, String errorCode) {
        return ApiErrorResponse.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }

    public static ApiErrorResponse of(String message, String errorCode, List<FieldError> errors) {
        return ApiErrorResponse.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .errors(errors)
                .build();
    }

    @Override
    public String toString() {
        return """ 
                {
                   "success": false,
                   "message": %s,
                   "errorCode": %s
                }
                """.formatted(message, errorCode);
    }
}
