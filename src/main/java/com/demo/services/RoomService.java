package com.demo.services;

import com.demo.dtos.request.CreateRoomRequest;
import com.demo.dtos.request.UpdateRoomRequest;
import com.demo.dtos.response.RoomDetailsDto;
import com.demo.dtos.response.RoomDto;
import com.demo.dtos.response.RoomImageDto;
import com.demo.dtos.response.RoomListDto;
import com.demo.entities.Room;
import com.demo.utils.PaginatedResponse;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    RoomDto createRoom(@Valid CreateRoomRequest request);
    Room findRoomById(UUID id);
    PaginatedResponse<RoomListDto> getRooms(String name, Boolean deleted, Integer capacityLessThan, Integer capacityMoreThan, String sortOrder, Integer page, Integer size);
    RoomDetailsDto getRoomById(UUID id);
    RoomDetailsDto updateRoom(UUID id, UpdateRoomRequest request);
    void deleteRoom(UUID id, Boolean deleted);
    List<RoomImageDto> addImages(UUID roomId, List<MultipartFile> files);
    void removeImage(UUID roomId, UUID imageId);
}
