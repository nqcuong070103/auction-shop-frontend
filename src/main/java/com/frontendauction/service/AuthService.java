package com.frontendauction.service;

import com.frontendauction.model.LoginResult;

import java.util.concurrent.CompletableFuture;

public interface AuthService {

    CompletableFuture<LoginResult> login(String username, String password);
}
