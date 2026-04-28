package com.demo.services;

import com.demo.dtos.request.CreateReservationRequest;
import com.demo.dtos.response.ReservationDetailsDto;
import com.demo.dtos.response.ReservationListDto;
import com.demo.utils.PaginatedResponse;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ReservationService {
    ReservationDetailsDto createReservation(@Valid CreateReservationRequest request);

    PaginatedResponse<?> getReservations(UUID userId, UUID roomId, String status, LocalDateTime from, LocalDateTime to, Integer page, Integer size);
    PaginatedResponse<ReservationListDto> getOwnReservations(UUID roomId, String status, LocalDateTime from, LocalDateTime to, Integer page, Integer size);

    ReservationDetailsDto getReservation(UUID id);

    void cancelReservation(UUID id);

    ReservationListDto resumeReservation(UUID id);

    ReservationListDto acceptReservation(UUID id);

    ReservationListDto rejectReservation(UUID id);
}
