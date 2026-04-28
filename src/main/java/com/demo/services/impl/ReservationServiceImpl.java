package com.demo.services.impl;

import com.demo.dtos.request.CreateReservationRequest;
import com.demo.dtos.response.ReservationDetailsDto;
import com.demo.dtos.response.ReservationListDto;
import com.demo.entities.Reservation;
import com.demo.entities.Room;
import com.demo.entities.enums.ReservationStatus;
import com.demo.entities.enums.Role;
import com.demo.exceptions.custom.ConflictException;
import com.demo.exceptions.custom.ForbiddenException;
import com.demo.exceptions.custom.NotFoundException;
import com.demo.mappers.ReservationMapper;
import com.demo.repositories.ReservationRepository;
import com.demo.repositories.RoomRepository;
import com.demo.repositories.specifications.ReservationSpecification;
import com.demo.services.ReservationService;
import com.demo.services.RoomService;
import com.demo.utils.PaginatedResponse;
import com.demo.utils.SecurityUtils;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final RoomService roomService;
    private final RoomRepository roomRepository;
    private final ReservationMapper reservationMapper;
    private final ReservationRepository reservationRepository;

    private void validateReservationDate(LocalDateTime startTime, LocalDateTime endTime){
        LocalDateTime now = LocalDateTime.now();

        //check that startTime and endTime is in the future
        if(startTime.isBefore(now)) throw new ValidationException("Start time must be in the future.");
        if(endTime.isBefore(now)) throw new ValidationException("End time must be in the future.");

        //check that startTime is before or equal to endTime
        if(startTime.isAfter(endTime) || startTime.isEqual(endTime))
            throw new ValidationException("Start time must be before end time.");
    }

    @Override
    @Transactional
    public ReservationDetailsDto createReservation(CreateReservationRequest request) {
        validateReservationDate(request.startTime(), request.endTime());

        //check that room exist with this id
        Room existRoom = roomService.findRoomById(request.roomId());
        if(existRoom.isDeleted())
            throw new NotFoundException("Room not found with this id: " + existRoom.getId());

        //check that reservation is valid for this room by fetching all the reservations
        checkOverlapping(request.startTime(), request.endTime(), existRoom.getId());

        //create the reservation
        Reservation reservation = reservationMapper.toEntity(request);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setRoom(existRoom);

        Reservation createdRoom = reservationRepository.save(reservation);

        return reservationMapper.toDto(createdRoom);
    }

    @Override
    public PaginatedResponse<?> getReservations(UUID userId, UUID roomId, String status, LocalDateTime from, LocalDateTime to, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        boolean isUser = SecurityUtils.hasRole(Role.ROLE_USER.name());

        //User can view all active reservations only.
        if (isUser) {
            return getAvailability(roomId, from, to, pageable);
        } else {
            return getAdminReservations(userId, roomId, status, from, to, pageable);
        }
    }

    @Override
    public PaginatedResponse<ReservationListDto> getOwnReservations(UUID roomId, String status, LocalDateTime from, LocalDateTime to, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Specification<Reservation> dateSpec = Specification.where(ReservationSpecification.startAfter(from)).and(ReservationSpecification.endBefore(to));

        Specification<Reservation> spec = buildSpecification(SecurityUtils.getCurrentUserId(), roomId, status, dateSpec);
        Page<Reservation> pageResult = reservationRepository.findAll(spec, pageable);

        return PaginatedResponse.from(pageResult, reservationMapper::toListDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationDetailsDto getReservation(UUID id) {
        Reservation reservation;
        boolean isUser = SecurityUtils.hasRole(Role.ROLE_USER.name());

        //User can only view his reservation details
        if(isUser){
            reservation = getOwnedReservation(id);
        }else{//Admin can view any reservation details
            reservation = findReservationById(id);
        }
        return reservationMapper.toDto(reservation);
    }

    @Override
    @Transactional
    public void cancelReservation(UUID id) {
        //User can delete only his reservation
        Reservation reservation = getOwnedReservation(id);

        ReservationStatus status = reservation.getStatus();
        if(status != ReservationStatus.ACCEPTED && status != ReservationStatus.PENDING)
            throw new ForbiddenException("You can't cancel %s reservation".formatted(status));

        reservation.setStatus(ReservationStatus.CANCELLED);
    }

    @Override
    @Transactional
    public ReservationListDto resumeReservation(UUID id) {
        Reservation reservation;
        reservation = getOwnedReservation(id);
        if(reservation.getStatus() != ReservationStatus.CANCELLED)
            throw new ForbiddenException("You can resume only cancelled reservations");

        //check that reservation date is still valid
        validateReservationDate(reservation.getStartTime(), reservation.getEndTime());

        //check if it is overlapped with other reservations
        checkOverlapping(reservation.getStartTime(), reservation.getEndTime(), reservation.getRoom().getId());

        reservation.setStatus(ReservationStatus.PENDING);
        return reservationMapper.toListDto(reservation);
    }

    @Override
    @Transactional
    public ReservationListDto acceptReservation(UUID id) {
        Reservation reservation = findReservationById(id);

        validateReservationDate(reservation.getStartTime(), reservation.getEndTime());

        if(reservation.getStatus() != ReservationStatus.PENDING)
            throw new ConflictException("You can accept only pending reservations");

        //lock the room to accept the reservation for it.
        Room room = roomRepository.findByIdForUpdate(reservation.getRoom().getId()).orElseThrow(() -> new NotFoundException("Room not found with this id: " + reservation.getRoom().getId()));
        if(room.isDeleted() == true)
            throw new IllegalStateException("Room is deleted");

        checkOverlapping(reservation.getStartTime(), reservation.getEndTime(), room.getId());
        reservation.setStatus(ReservationStatus.ACCEPTED);

        return reservationMapper.toListDto(reservation);
    }

    @Override
    @Transactional
    public ReservationListDto rejectReservation(UUID id) {
        Reservation reservation = findReservationById(id);

        if(reservation.getStatus() != ReservationStatus.PENDING)
            throw new ConflictException("You can reject only pending reservations");

        reservation.setStatus(ReservationStatus.REJECTED);

        return reservationMapper.toListDto(reservation);
    }

    // ---------------------------------------- Helper methods ---------------------------------------------------
    private Reservation getOwnedReservation(UUID id){
        Specification<Reservation> specification = Specification
                .where(ReservationSpecification.hasUserId(SecurityUtils.getCurrentUserId()))
                .and(ReservationSpecification.hasId(id));

        return reservationRepository.findOne(specification).orElseThrow(() -> new NotFoundException("Reservation not found with this id: " + id));
    }

    private Reservation findReservationById(UUID id){
        return reservationRepository.findById(id).orElseThrow(() -> new NotFoundException("Reservation not found with this id: " + id));
    }

    private void checkOverlapping(LocalDateTime startTime, LocalDateTime endTime, UUID roomId){
        boolean isOverlapped = reservationRepository.hasOverlap(startTime, endTime, roomId);
        if(isOverlapped)
            throw new ConflictException("Room already reserved in this time range");
    }

    private Specification<Reservation> buildSpecification(UUID userId, UUID roomId, String status,  Specification<Reservation> dateSpec) {
        return Specification
                .where(ReservationSpecification.hasUserId(userId))
                .and(ReservationSpecification.hasRoomId(roomId))
                .and(ReservationSpecification.hasStatus(status))
                .and(dateSpec);
    }

    private PaginatedResponse<ReservationListDto> getAvailability(UUID roomId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        if((from == null && to != null) || (from != null && to == null))
            throw new ValidationException("Both from and to must be applied together");

        //User can filter rooms based on if room is busy between from and to dates.
        Specification<Reservation> spec = buildSpecification(null, roomId, ReservationStatus.ACCEPTED.name(), ReservationSpecification.overlaps(from, to));
        Page<Reservation> pageResult = reservationRepository.findAll(spec, pageable);
        return PaginatedResponse.from(pageResult, reservationMapper::toListDto);
    }

    private PaginatedResponse<ReservationDetailsDto> getAdminReservations(UUID userId, UUID roomId, String status, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Specification<Reservation> dateSpec = Specification.where(ReservationSpecification.startAfter(from)).and(ReservationSpecification.endBefore(to));
        Specification<Reservation> spec = buildSpecification(userId, roomId, status, dateSpec);

        Page<Reservation> pageResult = reservationRepository.findAll(spec, pageable);

        return PaginatedResponse.from(pageResult, reservationMapper::toDto);
    }
}
