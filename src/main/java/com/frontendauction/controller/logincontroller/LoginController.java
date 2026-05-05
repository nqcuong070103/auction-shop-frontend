package com.frontendauction.controller.logincontroller;

import com.frontendauction.auth.AuthService;
import com.frontendauction.auth.HttpAuthService;
import com.frontendauction.auth.LoginResult;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private final AuthService authService;

    public LoginController() {
        this(new HttpAuthService());
    }

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

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

        authService.login(username, password)
                .thenAccept(result -> Platform.runLater(() -> handleResponse(result)))
                .exceptionally(exception -> {
                    Platform.runLater(() -> {
                        setLoading(false);
                        showError(resolveErrorMessage(exception));
                    });
                    return null;
                });
    }

    private void handleResponse(LoginResult result) {
        setLoading(false);

        if (result.success()) {
            hideError();
            System.out.println("Login success. Token = " + result.token());
            return;
        }

        showError(result.errorMessage());
    }

    private String resolveErrorMessage(Throwable exception) {
        Throwable cause = exception.getCause() == null ? exception : exception.getCause();
        String message = cause.getMessage();

        if (message == null || message.isBlank()) {
            return "Can't connected to server";
        }

        return message;
    }
}
