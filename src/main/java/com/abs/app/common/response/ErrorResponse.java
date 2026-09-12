package com.abs.app.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private boolean success;
    private String message;

    public ErrorResponse(boolean success, String message, String error) {
        this.success = success;
        this.message = message;
    }
}
