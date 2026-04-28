package com.demo.repositories.specifications;

import com.demo.entities.Room;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class RoomSpecification {
    public static Specification<Room> filterByName(String name) {
        return (root, query, cb) -> {
            if (name == null) return null;
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Room> filterByCapacityLessThan(Integer number) {
        return (root, query, cb) -> {
            if (number == null) return null;
            return cb.lessThan(root.get("capacity"), number);
        };
    }

    public static Specification<Room> filterByCapacityMoreThan(Integer number) {
        return (root, query, cb) -> {
            if (number == null) return null;
            return cb.greaterThan(root.get("capacity"), number);
        };
    }

    public static Specification<Room> filterByDeleted(Boolean deleted) {
        return (root, query, cb) -> {
            if (deleted == null) return null;
            return cb.equal(root.get("deleted"), deleted);
        };
    }

    public static Specification<Room> hasId(UUID id) {
        return (root, query, cb) -> {
            if (id == null) return null;
            return cb.equal(root.get("id"), id);
        };
    }

}
