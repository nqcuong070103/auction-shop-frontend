package com.frontendauction.service;

import com.frontendauction.model.LoginResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MockAuthService implements AuthService {

    @Override
    public CompletableFuture<LoginResult> login(String username, String password) {
        return CompletableFuture.supplyAsync(
                () -> authenticate(username, password),
                CompletableFuture.delayedExecutor(500, TimeUnit.MILLISECONDS)
        );
    }

    private LoginResult authenticate(String username, String password) {
        if ("admin_01".equals(username) && "password123".equals(password)) {
            return LoginResult.success("mock-token-admin-01");
        }

        if ("seller_01".equals(username) && "password123".equals(password)) {
            return LoginResult.success("mock-token-seller-01");
        }

        return LoginResult.failure("Invalid username or password in mock mode");
    }
}
