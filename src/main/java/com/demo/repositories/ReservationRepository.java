package com.demo.repositories;

import com.demo.entities.Reservation;
import com.demo.entities.Room;
import com.demo.entities.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID>, JpaSpecificationExecutor<Reservation> {

    @Query("""
            SELECT COUNT(r) > 0
            FROM Reservation r
            WHERE (:startTime < r.endTime AND :endTime > r.startTime)
                  AND r.room.id = :roomId
                  AND r.status = 'ACCEPTED'
            """)
    boolean hasOverlap(LocalDateTime startTime, LocalDateTime endTime, UUID roomId);

    @EntityGraph(attributePaths = {"room", "user"})
    Page<Reservation> findAll(Specification<Reservation> spec, Pageable pageable);

    @Modifying
    @Query("""
            UPDATE Reservation r
            SET r.status = 'EXPIRED'
            WHERE r.status = 'PENDING'
            AND r.endTime < :now
            """)
    int expirePendingReservations(LocalDateTime now);

    @Modifying
    @Query("""
            UPDATE Reservation r
            SET r.status = 'COMPLETED'
            WHERE r.status = 'ACCEPTED'
            AND r.endTime < :now
            """)
    int completeAcceptedReservations(LocalDateTime now);

    boolean existsByRoomAndStatus(Room room, ReservationStatus status);
}
