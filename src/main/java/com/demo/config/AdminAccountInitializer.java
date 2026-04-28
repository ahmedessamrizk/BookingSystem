package com.demo.config;

import com.demo.entities.User;
import com.demo.entities.enums.Role;
import com.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdminAccountInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${SUPERADMIN_USERNAME}")
    private String username;

    @Value("${SUPERADMIN_EMAIL}")
    private String email;

    @Value("${SUPERADMIN_PASSWORD}")
    private String password;

    private Role role = Role.ROLE_SUPERADMIN;

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.existsByEmail(email)){
            log.info("Super admin already exists!");
           return;
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        userRepository.save(user);
        log.info("Super admin account is created successfully!");
    }
}
