package com.demo.controllers;

import com.demo.dtos.request.CreateReservationRequest;
import com.demo.dtos.response.ReservationDetailsDto;
import com.demo.dtos.response.ReservationListDto;
import com.demo.services.ReservationService;
import com.demo.utils.ApiResponse;
import com.demo.utils.PaginatedResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationDetailsDto>> createReservation(@Valid @RequestBody CreateReservationRequest request){
        ReservationDetailsDto createdReservation = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Reservation is created successfully", createdReservation));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<?>>> getReservations(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID roomId,
            @RequestParam(required = false) @Pattern(regexp = "EXPIRED|COMPLETED|ACCEPTED|REJECTED|PENDING|CANCELLED") String status,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
            ){
        PaginatedResponse<?> reservations = reservationService.getReservations(userId, roomId, status, from, to, page, size);
        return ResponseEntity.ok(ApiResponse.success("Reservations are fetched successfully", reservations));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PaginatedResponse<ReservationListDto>>> getOwnReservations(
            @RequestParam(required = false) UUID roomId,
            @RequestParam(required = false) @Pattern(regexp = "EXPIRED|COMPLETED|ACCEPTED|REJECTED|PENDING|CANCELLED") String status,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ){
        PaginatedResponse<ReservationListDto> reservations = reservationService.getOwnReservations(roomId, status, from, to, page, size);
        return ResponseEntity.ok(ApiResponse.success("Reservations are fetched successfully", reservations));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationDetailsDto>> getReservation(@PathVariable UUID id){
        ReservationDetailsDto reservation = reservationService.getReservation(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation is fetched successfully", reservation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(@PathVariable UUID id){
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/resume")
    public ResponseEntity<ApiResponse<ReservationListDto>> resumeReservation(@PathVariable UUID id){
        ReservationListDto reservation = reservationService.resumeReservation(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation is resumed successfully", reservation));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<ReservationListDto>> acceptReservation(@PathVariable UUID id){
        ReservationListDto reservation = reservationService.acceptReservation(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation is accepted successfully", reservation));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<ReservationListDto>> rejectReservation(@PathVariable UUID id){
        ReservationListDto reservation = reservationService.rejectReservation(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation is rejected successfully", reservation));
    }

}
