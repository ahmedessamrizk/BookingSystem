package com.demo.controllers;

import com.demo.dtos.request.UpdateRoleDto;
import com.demo.dtos.response.UserDto;
import com.demo.dtos.response.UserListDto;
import com.demo.entities.enums.Role;
import com.demo.security.UserPrincipal;
import com.demo.services.UserService;
import com.demo.utils.ApiResponse;
import com.demo.utils.PaginatedResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<UserListDto>>> getUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean deleted,
            @RequestParam(required = false) @Pattern(regexp = "ROLE_USER|ROLE_ADMIN") String role,
            @RequestParam(defaultValue = "1")@Positive int page,
            @RequestParam(defaultValue = "10") @Positive int size){

        PaginatedResponse<UserListDto> users = userService.getUsers(email, deleted, role, page, size);
        return ResponseEntity.ok(ApiResponse.success("Users are fetched successfully", users));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> getProfile(){
        UserDto currentUser = userService.getProfile();
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", currentUser));
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID id){
        userService.deleteUserById(id, true);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping({"/{id}/restore"})
    public ResponseEntity<ApiResponse> restoreUser(@PathVariable("id") UUID id){
        userService.deleteUserById(id, false);
        return ResponseEntity.ok(ApiResponse.success("User is restored successfully", null));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<ApiResponse<UserDto>> updateRole(@PathVariable("id") UUID id,@Valid @RequestBody UpdateRoleDto request){
        UserDto updatedUser = userService.updateRole(id, request);
        return ResponseEntity.ok(ApiResponse.success("User role is updated successfully", updatedUser));
    }
}
