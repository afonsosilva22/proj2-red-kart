package com.example.desktop.services;

import com.example.desktop.models.BlacklistEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class BlacklistEntryService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    private final String BASE_URL = "http://localhost:8080/api/blacklist-entries";

    public BlacklistEntry create(BlacklistEntry blacklistEntry) throws Exception {
        String json = gson.toJson(blacklistEntry);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return gson.fromJson(response.body(), BlacklistEntry.class);
        } else {
            throw new RuntimeException("Failed to create blacklist entry: " + response.body());
        }
    }

    public List<BlacklistEntry> getAllBlacklistEntries() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/get/all"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            Type listType = new TypeToken<List<BlacklistEntry>>() {}.getType();
            return gson.fromJson(response.body(), listType);
        } else {
            throw new RuntimeException("Failed to fetch blacklist entries: " + response.body());
        }
    }

    public BlacklistEntry getBlacklistEntryById(Integer id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/get/" + id))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return gson.fromJson(response.body(), BlacklistEntry.class);
        } else {
            throw new RuntimeException("Failed to fetch blacklist entry: " + response.body());
        }
    }

    public void reinstateCustomer(Integer customerId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/reinstate/" + customerId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Failed to reinstate customer: " + response.body());
        }
    }
}