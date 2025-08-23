package com.TreadX.utils.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private String message;
    private T data;
    private boolean success;
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(message, data, true);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message, null, false);
    }
}
