package com.demo.services.impl;

import com.demo.dtos.request.LoginRequest;
import com.demo.dtos.request.SignupRequest;
import com.demo.dtos.response.LoginResponse;
import com.demo.dtos.response.UserDto;
import com.demo.entities.User;
import com.demo.entities.enums.Role;
import com.demo.exceptions.custom.ConflictException;
import com.demo.mappers.UserMapper;
import com.demo.repositories.UserRepository;
import com.demo.security.JwtService;
import com.demo.services.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public UserDto createUser(SignupRequest request) {
        User user = userMapper.toEntity(request);

        // check username and email are unique
        if (userRepository.existsByEmail(user.getEmail().trim().toLowerCase()))
            throw new ConflictException("Email already exists");

        if (userRepository.existsByUsername(user.getUsername().trim()))
            throw new ConflictException("Username already exists");

        // encode password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        user.setRole(Role.ROLE_USER);
        User createdUser = userRepository.save(user);

        // return data
        return userMapper.toDto(createdUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        String token = jwtService.generateToken(authentication);
        LoginResponse response = LoginResponse.builder().token(token).build();
        return response;
    }
}
