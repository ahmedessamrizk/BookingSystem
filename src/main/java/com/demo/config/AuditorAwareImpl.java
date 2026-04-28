package com.demo.config;

import com.demo.entities.User;
import com.demo.security.UserPrincipal;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<User> {
    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

        User user = new User();
        user.setId(principal.getId());
        user.setUsername(principal.getName());

        return Optional.of(user);
    }
}
