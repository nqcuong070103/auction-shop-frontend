package com.frontendauction.controller.logincontroller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class LoginController {

    private static final String LOGIN_URL = "http://localhost:1234/auth/login";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    public void initialize() {
        hideError();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void hideError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    private boolean validateInput(String username, String password) {
        if (username.isBlank() || password.isBlank()) {
            showError("Please type fully username and password!");
            return false;
        }

        if (!username.matches("^[a-zA-Z0-9_]{6,20}$")) {
            showError("Username need length 6-20 characters and only have words, numbers, _");
            return false;
        }

        if (password.length() < 10 || password.contains(" ")) {
            showError("Password must have at least 10 characters and not include space");
            return false;
        }

        return true;
    }
    //Doi trang thai cua button Login
    private void setLoading(boolean loading) {
        loginButton.setDisable(loading);
        loginButton.setText(loading ? "Loading..." : "Login");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        hideError();

        if (!validateInput(username, password)) {
            return;
        }

        setLoading(true);

        try {
            String body = objectMapper.writeValueAsString(
                    Map.of("username", username, "password", password)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LOGIN_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                    .thenAccept(response -> Platform.runLater(() -> handleResponse(response)))
                    .exceptionally(exception -> {
                        Platform.runLater(() -> {
                            setLoading(false);
                            showError("Can't connected to server");
                        });
                        return null;
                    });

        } catch (Exception exception) {
            setLoading(false);
            showError("Can't created login request");
        }
    }

    private void handleResponse(HttpResponse<String> response) {
        setLoading(false);

        try {
            JsonNode json = objectMapper.readTree(response.body());

            if (response.statusCode() == 201) {
                String token = json.path("token").asText("");
                hideError();
                System.out.println("Login success. Token = " + token);
                return;
            }

            String errorMessage = json.path("error").asText("Login failed!");
            showError(errorMessage);

        } catch (Exception exception) {
            showError("Response from server invalid");
        }
    }
}
