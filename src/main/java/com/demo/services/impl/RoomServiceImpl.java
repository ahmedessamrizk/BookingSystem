package com.demo.services.impl;

import com.demo.dtos.request.CreateRoomRequest;
import com.demo.dtos.request.UpdateRoomRequest;
import com.demo.dtos.response.RoomDetailsDto;
import com.demo.dtos.response.RoomDto;
import com.demo.dtos.response.RoomImageDto;
import com.demo.dtos.response.RoomListDto;
import com.demo.entities.Room;
import com.demo.entities.RoomImage;
import com.demo.entities.enums.ReservationStatus;
import com.demo.entities.enums.Role;
import com.demo.exceptions.custom.ConflictException;
import com.demo.exceptions.custom.NotFoundException;
import com.demo.mappers.RoomMapper;
import com.demo.repositories.ReservationRepository;
import com.demo.repositories.RoomImageRepository;
import com.demo.repositories.RoomRepository;
import com.demo.repositories.specifications.RoomSpecification;
import com.demo.services.FileStorageService;
import com.demo.services.RoomService;
import com.demo.utils.PaginatedResponse;
import com.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomMapper roomMapper;
    private final RoomRepository roomRepository;
    private final FileStorageService fileStorageService;
    private final RoomImageRepository roomImageRepository;
    private final ReservationRepository reservationRepository;

    public Room findRoomById(UUID id){
        return roomRepository.findById(id).orElseThrow(() -> new NotFoundException("Room not found with this id: " + id));
    }

    @Override
    public RoomDto createRoom(CreateRoomRequest request) {
        if(roomRepository.existsByName(request.name()))
            throw new ConflictException("Room already exist with this name: " + request.name());

        Room createdRoom = roomRepository.save(roomMapper.toEntity(request));
        return roomMapper.toDto(createdRoom);
    }

    @Override
    public PaginatedResponse<RoomListDto> getRooms(String keyword, Boolean deleted, Integer capacityLessThan, Integer capacityMoreThan, String sortOrder, Integer page, Integer size) {
        Sort sort = sortOrder.equals("asc")? Sort.by("capacity").ascending() : Sort.by("capacity").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Boolean isDeleted = SecurityUtils.hasRole(Role.ROLE_USER.name())? Boolean.FALSE : deleted;
        Specification<Room> specification = Specification
                .where(RoomSpecification.filterByName(keyword))
                .and(RoomSpecification.filterByCapacityLessThan(capacityLessThan))
                .and(RoomSpecification.filterByCapacityMoreThan(capacityMoreThan))
                .and(RoomSpecification.filterByDeleted(isDeleted));

        Page<Room> roomsPage = roomRepository.findAll(specification, pageable);

        PaginatedResponse<RoomListDto> repsonse = PaginatedResponse.<RoomListDto>builder()
                .content(roomsPage.map(roomMapper::toListDto).getContent())
                .page(roomsPage.getNumber() + 1)
                .size(roomsPage.getSize())
                .totalPages(roomsPage.getTotalPages())
                .totalElements(roomsPage.getTotalElements())
                .build();

        return repsonse;
    }

    @Override
    public RoomDetailsDto getRoomById(UUID id) {
        Specification<Room> spec = Specification.where(RoomSpecification.hasId(id));

        //user can't access deleted room
        boolean isUser = SecurityUtils.hasRole(Role.ROLE_USER.name());
        if(isUser){
            spec = spec.and(RoomSpecification.filterByDeleted(false));
        }

        Room fetchRoom = roomRepository.findOne(spec).orElseThrow(() -> new NotFoundException("Room not found"));
        fetchRoom.getImages().stream().map(roomMapper::toImageDto).toList();
        return roomMapper.toDetailsDto(fetchRoom);
    }

    @Override
    @Transactional
    public RoomDetailsDto updateRoom(UUID id, UpdateRoomRequest request) {
        //check that id is valid
        Room roomExist = findRoomById(id);

        //if request contains name, check it is unique
        String roomName = request.name();
        if(roomName != null){
            if(roomRepository.existsByName(roomName.toLowerCase()))
                throw new ConflictException("Room already exist with this name: " + roomName);
            roomExist.setName(roomName.trim());
        }

        //update with the given data and save.
        if(request.capacity() != null)
            roomExist.setCapacity(request.capacity());

        return roomMapper.toDetailsDto(roomExist);
    }

    @Override
    @Transactional
    public void deleteRoom(UUID id, Boolean deleted) {
        Room existRoom = findRoomById(id);

        boolean hasReservations = reservationRepository.existsByRoomAndStatus(existRoom, ReservationStatus.ACCEPTED);
        if(hasReservations)
            throw new IllegalStateException("Room has scheduled reservations");

        existRoom.setDeleted(deleted);
    }

    @Override
    @Transactional
    public List<RoomImageDto> addImages(UUID roomId, List<MultipartFile> files) {
        Room existRoom = this.findRoomById(roomId);

        List<UploadedFile> uploadedFiles = fileStorageService.uploadFiles(files, "rooms/%s".formatted(existRoom.getId()));
        List<RoomImage> images = uploadedFiles.stream().map(
                file -> RoomImage.builder()
                        .room(existRoom)
                        .url(file.getUrl())
                        .publicId(file.getPublicId())
                        .build()).toList();

        existRoom.getImages().addAll(images);
        roomRepository.save(existRoom);

        return existRoom.getImages().stream().map(roomMapper::toImageDto).toList();
    }

    @Override
    public void removeImage(UUID roomId, UUID imageId) {
        Room existRoom = this.findRoomById(roomId);
        RoomImage image = roomImageRepository.findById(imageId).orElseThrow(() -> new NotFoundException("Image not found"));

        fileStorageService.delete(image.getPublicId());
        roomImageRepository.delete(image);
    }

}
