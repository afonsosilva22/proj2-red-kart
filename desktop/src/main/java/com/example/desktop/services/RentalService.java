package com.example.desktop.services;

import com.example.desktop.models.Rental;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class RentalService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    private final String BASE_URL = "http://localhost:8080/api/rentals";

    public Rental create(Rental rental) throws Exception {
        String json = gson.toJson(rental);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return gson.fromJson(response.body(), Rental.class);
        } else {
            throw new RuntimeException("Failed to create rental record: " + response.body());
        }
    }

    public List<Rental> getAllRentals() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/get/all"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            Type listType = new TypeToken<List<Rental>>() {}.getType();
            return gson.fromJson(response.body(), listType);
        } else {
            throw new RuntimeException("Failed to fetch rentals from backend: " + response.body());
        }
    }

    public Rental update(Integer id, Rental rental) throws Exception {
        String json = gson.toJson(rental);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/update/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return gson.fromJson(response.body(), Rental.class);
        } else {
            throw new RuntimeException("Failed to update rental record: " + response.body());
        }
    }
}