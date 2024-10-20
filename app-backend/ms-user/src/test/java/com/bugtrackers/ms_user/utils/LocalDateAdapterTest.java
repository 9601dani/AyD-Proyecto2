package com.bugtrackers.ms_user.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class LocalDateAdapterTest {

    private LocalDateAdapater localDateAdapter;
    private Gson gson;

    @BeforeEach
    void setUp() {
        localDateAdapter = new LocalDateAdapater();
        gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, localDateAdapter).create();
    }

    @Test
    void testWrite() throws IOException {
        LocalDate date = LocalDate.of(2024, 10, 8);
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        localDateAdapter.write(jsonWriter, date);
        jsonWriter.close();

        assertEquals("\"2024-10-08\"", stringWriter.toString());
    }

    @Test
    void testRead() throws IOException {
        String json = "\"2024-10-08\"";

        StringReader stringReader = new StringReader(json);
        JsonReader jsonReader = new JsonReader(stringReader);
        LocalDate result = localDateAdapter.read(jsonReader);

        assertEquals(LocalDate.of(2024, 10, 8), result);
    }

    @Test
    void testGsonIntegration() {
        LocalDate date = LocalDate.of(2024, 10, 8);

        String json = gson.toJson(date);
        assertEquals("\"2024-10-08\"", json);

        LocalDate deserializedDate = gson.fromJson(json, LocalDate.class);
        assertEquals(date, deserializedDate);
    }
}
