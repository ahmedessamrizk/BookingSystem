package com.demo.services;

import com.demo.dtos.request.LoginRequest;
import com.demo.dtos.request.SignupRequest;
import com.demo.dtos.response.LoginResponse;
import com.demo.dtos.response.UserDto;

public interface AuthService {
    UserDto createUser(SignupRequest request);
    LoginResponse login(LoginRequest request);
}
