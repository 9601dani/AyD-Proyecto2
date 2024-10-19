package com.bugtrackers.ms_email.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class LocalDateTimeAdapterTest {

    private LocalDateTimeAdapter localDateTimeAdapter;
    private Gson gson;

    @BeforeEach
    void setUp() {
        localDateTimeAdapter = new LocalDateTimeAdapter();
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, localDateTimeAdapter).create();
    }

    @Test
    void testWrite() throws IOException {
        LocalDateTime dateTime = LocalDateTime.of(2024, 10, 8, 12, 30);
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        localDateTimeAdapter.write(jsonWriter, dateTime);
        jsonWriter.close();

        assertEquals("\"2024-10-08T12:30:00\"", stringWriter.toString());
    }

    @Test
    void testRead() throws IOException {
        String json = "\"2024-10-08T12:30:00\"";

        StringReader stringReader = new StringReader(json);
        JsonReader jsonReader = new JsonReader(stringReader);
        LocalDateTime result = localDateTimeAdapter.read(jsonReader);

        assertEquals(LocalDateTime.of(2024, 10, 8, 12, 30), result);
    }

    @Test
    void testGsonIntegration() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 10, 8, 12, 30);

        String json = gson.toJson(dateTime);
        assertEquals("\"2024-10-08T12:30:00\"", json);

        LocalDateTime deserializedDateTime = gson.fromJson(json, LocalDateTime.class);
        assertEquals(dateTime, deserializedDateTime);
    }
}
