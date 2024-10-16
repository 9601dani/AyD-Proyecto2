package com.bugtrackers.ms_user.dto.response;

public record ResponseString(
        String message
) {
    public ResponseString(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
