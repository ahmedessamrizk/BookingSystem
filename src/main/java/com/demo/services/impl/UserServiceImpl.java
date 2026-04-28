package com.demo.services.impl;

import com.demo.dtos.request.UpdateRoleDto;
import com.demo.dtos.response.UserDto;
import com.demo.dtos.response.UserListDto;
import com.demo.entities.User;
import com.demo.entities.enums.Role;
import com.demo.exceptions.custom.ForbiddenException;
import com.demo.exceptions.custom.NotFoundException;
import com.demo.mappers.UserMapper;
import com.demo.repositories.UserRepository;
import com.demo.repositories.specifications.UserSpecification;
import com.demo.services.UserService;
import com.demo.utils.PaginatedResponse;
import com.demo.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private User getUserById(UUID id){
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found with this id: " + id));
    }

    @Override
    public PaginatedResponse<UserListDto> getUsers(String email, Boolean deleted, String role, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Specification<User> spec = Specification
                .where(UserSpecification.hasEmail(email))
                .and(UserSpecification.isDeleted(deleted))
                .and(UserSpecification.hasRole(role))
                .and(UserSpecification.isNotSuperAdmin());

        Page<User> usersPage = userRepository.findAll(spec, pageable);

        PaginatedResponse<UserListDto> response = PaginatedResponse.<UserListDto>builder()
                .content(usersPage.getContent().stream().map(userMapper::toListDto).toList())
                .page(usersPage.getNumber()+1)
                .size(usersPage.getSize())
                .totalPages(usersPage.getTotalPages())
                .totalElements(usersPage.getTotalElements())
                .build();

        return response;
    }

    @Override
    public UserDto getProfile(){
        User fetchUser = getUserById(SecurityUtils.getCurrentUserId());
        return userMapper.toDto(fetchUser);
    }

    @Override
    @Transactional
    public void deleteUserById(UUID id, Boolean deleted) {
        User fetchUser = getUserById(id);
        //superAdmin can't be deleted/restored
        if(fetchUser.getRole() == Role.ROLE_SUPERADMIN)
            throw new ForbiddenException("Not allowed");

        //if currentUser is admin -> can't delete/restore admin
        if(SecurityUtils.hasRole(Role.ROLE_ADMIN.name()) && fetchUser.getRole().equals(Role.ROLE_ADMIN))
            throw new ForbiddenException("Not allowed");

        //can't delete/restore himself
        if(SecurityUtils.getCurrentUserId().equals(fetchUser.getId()))
            throw new ForbiddenException("Not allowed");

        fetchUser.setDeleted(deleted);
    }

    @Override
    public UserDto updateRole(UUID id, UpdateRoleDto request) {
        Role role = Role.valueOf(request.role());
        if(SecurityUtils.getCurrentUserId().equals(id))
            throw new ForbiddenException("Not allowed");

        User fetchUser = getUserById(id);

        //Prevent change role of superAdmin
        if(fetchUser.getRole().equals(Role.ROLE_SUPERADMIN))
            throw new ForbiddenException("Not allowed");

        fetchUser.setRole(role);
        return userMapper.toDto(fetchUser);
    }

}
