package com.example.desktop.services;

import com.example.desktop.models.Equipment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class EquipmentService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String BASE_URL = "http://localhost:8080/api/equipments"; // Adjust if backend mapping differs

    public Equipment create(Equipment equipment) throws Exception {
        String json = gson.toJson(equipment);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return gson.fromJson(response.body(), Equipment.class);
        } else {
            throw new RuntimeException("Failed to save equipment: " + response.body());
        }
    }

    public List<Equipment> getAllEquipment() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/get/all"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            Type listType = new TypeToken<List<Equipment>>() {}.getType();
            return gson.fromJson(response.body(), listType);
        } else {
            throw new RuntimeException("Failed to fetch equipment records: " + response.body());
        }
    }

    public Equipment update(Integer id, Equipment equipment) throws Exception {
        String json = gson.toJson(equipment);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/update/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return gson.fromJson(response.body(), Equipment.class);
        } else {
            throw new RuntimeException("Failed to update equipment: " + response.body());
        }
    }
}