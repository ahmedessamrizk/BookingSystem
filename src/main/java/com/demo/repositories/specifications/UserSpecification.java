package com.demo.repositories.specifications;

import com.demo.entities.User;
import com.demo.entities.enums.Role;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email == null) return null;
            return cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<User> isDeleted(Boolean deleted) {
        return (root, query, cb) -> {
            if (deleted == null) return null;
            return cb.equal(root.get("deleted"), deleted);
        };
    }

    public static Specification<User> hasRole(String role) {
        return (root, query, cb) -> {
            if (role == null) return null;
            return cb.equal(root.get("role"), role);
        };
    }

    public static Specification<User> isNotSuperAdmin() {
        return (root, query, cb) -> {
            return cb.notEqual(root.get("role"), Role.ROLE_SUPERADMIN);
        };
    }
}
