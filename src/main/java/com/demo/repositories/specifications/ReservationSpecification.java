package com.demo.repositories.specifications;

import com.demo.entities.Reservation;
import com.demo.entities.enums.ReservationStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReservationSpecification {
    public static Specification<Reservation> hasRoomId(UUID roomId) {
        return (root, query, cb) -> {
            if (roomId == null) return null;
            return cb.equal(root.get("room").get("id"), roomId);
        };
    }

    public static Specification<Reservation> hasUserId(UUID userId) {
        return (root, query, cb) -> {
            if (userId == null) return null;
            return cb.equal(root.get("user").get("id"), userId);
        };
    }

    public static Specification<Reservation> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            return cb.equal(root.get("status"), ReservationStatus.valueOf(status));
        };
    }

    public static Specification<Reservation> startAfter(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) return null;
            return cb.greaterThanOrEqualTo(root.get("startTime"), date);
        };
    }

    public static Specification<Reservation> endBefore(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) return null;
            return cb.lessThanOrEqualTo(root.get("endTime"), date);
        };
    }

    public static Specification<Reservation> overlaps(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from == null || to == null) return null;

            return cb.and(
                    cb.lessThan(root.get("startTime"), to),
                    cb.greaterThan(root.get("endTime"), from)
            );
        };
    }

    public static Specification<Reservation> hasId(UUID id) {
        return (root, query, cb) -> {
            return cb.equal(
                   root.get("id"), id
            );
        };
    }
}
