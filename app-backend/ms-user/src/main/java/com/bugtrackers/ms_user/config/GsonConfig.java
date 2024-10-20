package com.bugtrackers.ms_user.config;

import com.bugtrackers.ms_user.utils.LocalDateAdapater;
import com.bugtrackers.ms_user.utils.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GsonConfig {
    
    public static Gson createGsonWithAdapter() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapater())
                .create();
    }
}
