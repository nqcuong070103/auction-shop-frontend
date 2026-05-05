package com.frontendauction.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HttpAuthService implements AuthService {

    private static final String LOGIN_URL = "http://localhost:1234/auth/login";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpAuthService() {
        this(HttpClient.newHttpClient(), new ObjectMapper());
    }

    public HttpAuthService(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public CompletableFuture<LoginResult> login(String username, String password) {
        final String body;

        try {
            body = objectMapper.writeValueAsString(
                    Map.of("username", username, "password", password)
            );
        } catch (Exception exception) {
            return CompletableFuture.failedFuture(
                    new IllegalStateException("Can't created login request", exception)
            );
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(this::mapResponse);
    }

    private LoginResult mapResponse(HttpResponse<String> response) {
        try {
            JsonNode json = objectMapper.readTree(response.body());

            if (response.statusCode() == 201) {
                return LoginResult.success(json.path("token").asText(""));
            }

            String errorMessage = json.path("error").asText("Login failed!");
            return LoginResult.failure(errorMessage);
        } catch (Exception exception) {
            return LoginResult.failure("Response from server invalid");
        }
    }
}
