package com.bugtrackers.ms_auth.config;

import com.bugtrackers.ms_auth.utils.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

public class GsonConfig {
    
    public static Gson createGsonWithLocalDateTimeAdapter() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
}
