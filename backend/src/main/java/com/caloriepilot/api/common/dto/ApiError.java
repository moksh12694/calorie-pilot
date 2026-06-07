package com.caloriepilot.api.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String code,
        String message,
        Map<String, List<String>> fieldErrors
) {
    public static ApiError of(String code, String message) {
        return new ApiError(code, message, null);
    }

    public static ApiError validation(String message, Map<String, List<String>> fieldErrors) {
        return new ApiError("VALIDATION_ERROR", message, fieldErrors);
    }
}
