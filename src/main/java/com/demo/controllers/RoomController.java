package com.demo.controllers;

import com.demo.dtos.request.CreateRoomRequest;
import com.demo.dtos.request.UpdateRoomRequest;
import com.demo.dtos.response.RoomDetailsDto;
import com.demo.dtos.response.RoomDto;
import com.demo.dtos.response.RoomImageDto;
import com.demo.dtos.response.RoomListDto;
import com.demo.services.RoomService;
import com.demo.utils.ApiResponse;
import com.demo.utils.PaginatedResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rooms")
public class RoomController {
    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoomDto>> createRoom(@Valid @RequestBody CreateRoomRequest request){
        RoomDto createdRoom = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Room is created successfully", createdRoom));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<RoomListDto>>> getRooms(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean deleted,
            @RequestParam(required = false) @Positive Integer capacityLessThan,
            @RequestParam(required = false) @Positive Integer capacityMoreThan,
            @RequestParam(required = false, defaultValue = "asc") @Pattern(regexp = "asc|desc") String sortOrder,
            @RequestParam(defaultValue = "1")@Positive int page,
            @RequestParam(defaultValue = "10")@Positive int size
            ){
        PaginatedResponse<RoomListDto> rooms = roomService.getRooms(keyword, deleted, capacityLessThan, capacityMoreThan, sortOrder, page, size);
        return ResponseEntity.ok(ApiResponse.success("Rooms are fetched successfully", rooms));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomDetailsDto>> getRoomById(@PathVariable("id") @NotNull(message = "id is required") UUID id){
        return ResponseEntity.ok(ApiResponse.success("Room is fetched successfully", roomService.getRoomById(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomDetailsDto>> updateRoom(
            @PathVariable("id") @NotNull(message = "id is required") UUID id,
            @Valid @RequestBody UpdateRoomRequest request){
        RoomDetailsDto updatedRoom = roomService.updateRoom(id, request);
        return ResponseEntity.ok(ApiResponse.success("Room is updated successfully", updatedRoom));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("id") @NotNull(message = "id is required") UUID id){
        roomService.deleteRoom(id, true);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse> restoreRoom(@PathVariable("id") @NotNull(message = "id is required") UUID id){
        roomService.deleteRoom(id, false);
        return ResponseEntity.ok(ApiResponse.success("Room is restored successfully", null));
    }

    @PostMapping("/{roomId}/images")
    public ResponseEntity<ApiResponse<List<RoomImageDto>>> addImages(@PathVariable UUID roomId, @RequestParam List<MultipartFile> files){
        List<RoomImageDto> response = roomService.addImages(roomId, files);
        return ResponseEntity.ok(ApiResponse.success("Images for the room are uploaded successfully!", response));
    }

    @DeleteMapping("/{roomId}/images/{imageId}")
    public ResponseEntity<Void> removeImage(@PathVariable UUID roomId, @PathVariable UUID imageId){
        roomService.removeImage(roomId, imageId);
        return ResponseEntity.noContent().build();
    }
}
