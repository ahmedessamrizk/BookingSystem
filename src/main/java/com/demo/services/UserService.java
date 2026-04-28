package com.demo.services;

import com.demo.dtos.request.UpdateRoleDto;
import com.demo.dtos.response.UserDto;
import com.demo.dtos.response.UserListDto;
import com.demo.utils.PaginatedResponse;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public interface UserService {
    PaginatedResponse<UserListDto> getUsers(String email, Boolean deleted, String role, @Positive int page, @Positive int size);
    UserDto getProfile();
    void deleteUserById(UUID id, Boolean deleted);
    UserDto updateRole(UUID id, UpdateRoleDto request);
}
