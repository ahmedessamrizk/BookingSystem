package com.demo.scheduler;

import com.demo.repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationScheduler {
    private final ReservationRepository reservationRepository;

    @Transactional
    @Scheduled(fixedRate = 60000) // every 1 minute
    public void updateReservationStatuses() {
        LocalDateTime now = LocalDateTime.now();

        int expired = reservationRepository.expirePendingReservations(now);
        int completed = reservationRepository.completeAcceptedReservations(now);

        // optional logging
        if (expired > 0 || completed > 0) {
            log.info("Scheduler updated: expired=" + expired + ", completed=" + completed);
        }
    }
}
