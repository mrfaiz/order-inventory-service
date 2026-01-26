package com.example.common.exception;

import java.time.Instant;
import java.util.Map;

public record ApiError(
        Instant time,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldsError
) {

    public static ApiError of(int status,
                              String error,
                              String message,
                              String path ){
        return new ApiError(Instant.now(),status,error, message, path,Map.of());
    }

    public static ApiError of(int status,
                              String error,
                              String message,
                              String path, Map<String, String> fieldsError){
        return new ApiError(Instant.now(),status,error, message, path, fieldsError);
    }
}
