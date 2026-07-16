package com.app.service;

import com.app.dto.auth.AuthResponse;
import com.app.dto.auth.LoginRequest;
import com.app.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
