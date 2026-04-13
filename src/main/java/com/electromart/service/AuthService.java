package com.electromart.service;

import com.electromart.dto.LoginRequest;
import com.electromart.dto.RegisterRequest;
import com.electromart.entity.User;

public interface AuthService {

    User register(RegisterRequest request);

    String login(LoginRequest request);
}
