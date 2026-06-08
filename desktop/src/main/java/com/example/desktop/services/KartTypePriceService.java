package com.example.desktop.services;

import com.example.desktop.models.KartTypePrice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class KartTypePriceService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String BASE_URL = "http://localhost:8080/api/kart-type-prices";

    public KartTypePrice create(KartTypePrice kartTypePrice) throws Exception {
        String json = gson.toJson(kartTypePrice);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return gson.fromJson(response.body(), KartTypePrice.class);
        } else {
            throw new RuntimeException("Failed to create kart type: " + response.body());
        }
    }

    public List<KartTypePrice> getAllTypes() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/get/all"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            Type listType = new TypeToken<List<KartTypePrice>>() {}.getType();
            return gson.fromJson(response.body(), listType);
        } else {
            throw new RuntimeException("Failed to fetch kart categories: " + response.body());
        }
    }
}