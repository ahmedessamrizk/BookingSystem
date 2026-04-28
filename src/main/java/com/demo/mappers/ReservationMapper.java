package com.demo.mappers;

import com.demo.dtos.request.CreateReservationRequest;
import com.demo.dtos.response.ReservationDetailsDto;
import com.demo.dtos.response.ReservationListDto;
import com.demo.entities.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservationMapper {
    Reservation toEntity(CreateReservationRequest request);

    @Mapping(source = "user", target = "createdBy")
    @Mapping(source = "room", target = "room")
    ReservationDetailsDto toDto(Reservation reservation);

    @Mapping(source = "room", target = "room")
    ReservationListDto toListDto(Reservation reservation);
}
