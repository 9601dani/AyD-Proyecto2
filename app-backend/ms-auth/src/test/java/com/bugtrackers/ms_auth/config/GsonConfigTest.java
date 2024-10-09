package com.bugtrackers.ms_auth.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

public class GsonConfigTest {

    private Gson gson;

    @Test
    void testGsonConfigInstance() {
        GsonConfig gsonConfig = new GsonConfig();
        assertNotNull(gsonConfig);
    }

    @Test
    void testCreateGsonWithLocalDateTimeAdapter() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 10, 8, 12, 30);

        gson = GsonConfig.createGsonWithLocalDateTimeAdapter();
        String json = gson.toJson(dateTime);
        
        LocalDateTime deserializedDateTime = gson.fromJson(json, LocalDateTime.class);
        
        assertEquals(dateTime, deserializedDateTime);
    }
}
