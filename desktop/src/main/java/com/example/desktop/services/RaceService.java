package com.example.desktop.services;

import com.example.desktop.models.Race;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class RaceService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String BASE_URL = "http://localhost:8080/api/races";

    private final Gson gson = new GsonBuilder()
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    // Skip 'race' inside RaceKart
                    // Skip 'raceKart' inside Lap
                    return f.getName().equals("race") || f.getName().equals("raceKart");
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .create();

    public Race create(Race race) throws Exception {
        String json = gson.toJson(race);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            String body = response.body();
            if (body == null || body.isBlank()) {
                return race;
            }
            try {
                return gson.fromJson(body, Race.class);
            } catch (Exception e) {
                // Save succeeded but response body was not parseable (e.g. circular refs)
                return race;
            }
        } else {
            throw new RuntimeException("Server rejected race registration payload: " + response.body());
        }
    }

    public List<Race> getAllRaces() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/get/all"))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            Type listType = new TypeToken<List<Race>>(){}.getType();
            return gson.fromJson(response.body(), listType);
        } else {
            throw new RuntimeException("Failed to fetch races ledger list: " + response.body());
        }
    }
}