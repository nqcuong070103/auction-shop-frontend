package com.frontendauction.auth;

import java.util.concurrent.CompletableFuture;

public interface AuthService {

    CompletableFuture<LoginResult> login(String username, String password);
}
