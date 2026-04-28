package com.demo.repositories;

import com.demo.entities.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;

import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID>,  JpaSpecificationExecutor<Room> {
    boolean existsByName(String name);

    @EntityGraph(attributePaths = {"createdBy", "images"})
    Optional<Room> findOne(Specification<Room> specification);

    @Query("""
            SELECT r FROM Room r
            WHERE r.id = :id
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Room> findByIdForUpdate(UUID id);
}
