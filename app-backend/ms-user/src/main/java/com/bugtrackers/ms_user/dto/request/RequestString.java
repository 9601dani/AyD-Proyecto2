package com.bugtrackers.ms_user.dto.request;

public record RequestString(
        String message
) {
    public RequestString(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
