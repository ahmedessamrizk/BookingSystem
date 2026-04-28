package com.demo.mappers;

import com.demo.dtos.request.CreateRoomRequest;
import com.demo.dtos.request.UpdateRoomRequest;
import com.demo.dtos.response.RoomDetailsDto;
import com.demo.dtos.response.RoomDto;
import com.demo.dtos.response.RoomImageDto;
import com.demo.dtos.response.RoomListDto;
import com.demo.entities.Room;
import com.demo.entities.RoomImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomMapper {
    Room toEntity(CreateRoomRequest request);
    Room toEntity(UpdateRoomRequest request);

    RoomDto toDto(Room room);
    RoomListDto toListDto(Room room);

    @Mapping(source = "createdBy", target = "createdBy")
    RoomDetailsDto toDetailsDto(Room room);

    RoomImageDto toImageDto(RoomImage image);
}
